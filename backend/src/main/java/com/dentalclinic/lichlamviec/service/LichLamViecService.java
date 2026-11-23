package com.dentalclinic.lichlamviec.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ClinicProperties;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.dto.DoiLichLamViecDto;
import com.dentalclinic.lichlamviec.dto.LichHenAnhHuongDto;
import com.dentalclinic.lichlamviec.dto.LichLamViecCapNhatRequest;
import com.dentalclinic.lichlamviec.dto.LichLamViecDto;
import com.dentalclinic.lichlamviec.dto.LichLamViecKhoaRequest;
import com.dentalclinic.lichlamviec.dto.LichLamViecRequest;
import com.dentalclinic.lichlamviec.dto.LichTuanRequest;
import com.dentalclinic.lichlamviec.dto.SaoChepTuanDto;
import com.dentalclinic.lichlamviec.dto.SaoChepTuanRequest;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import com.dentalclinic.lichlamviec.repository.LichLamViecRepository;
import com.dentalclinic.security.UserPrincipal;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.entity.VaiTro;
import com.dentalclinic.taikhoan.repository.BacSiRepository;

@Service
@RequiredArgsConstructor
public class LichLamViecService {
    private static final int MAX_RANGE_DAYS = 62;
    // Lịch hẹn chưa khám xong, cần nhân viên xử lý khi buổi làm việc thay đổi
    private static final List<TrangThaiLichHen> PENDING_STATUSES = List.of(TrangThaiLichHen.da_dat, TrangThaiLichHen.da_den);

    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;
    private final BacSiRepository bacSiRepository;
    private final ClinicProperties clinicProperties;

    // Đọc bác sĩ qua quan hệ lazy nên cần transaction
    @Transactional(readOnly = true)
    public List<LichLamViecDto> list(LocalDate from, LocalDate to, Integer doctorId, UserPrincipal user) {
        if (to.isBefore(from) || from.plusDays(MAX_RANGE_DAYS).isBefore(to)) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE,
                    "Khoảng ngày không hợp lệ (tối đa " + MAX_RANGE_DAYS + " ngày)");
        }
        // Bác sĩ chỉ xem lịch của chính mình
        Integer filterId = doctorId;
        if (VaiTro.bac_si.name().equals(user.getVaiTro())) {
            filterId = bacSiRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan())
                    .orElseThrow(() -> new AppException(ResponseCode.DOCTOR_NOT_FOUND)).getMaBacSi();
        }
        return toDtos(lichLamViecRepository.findInRange(from, to, filterId));
    }

    // Lưu lưới tuần trong một transaction: một ô sai thì cả lần lưu bị hủy
    @Transactional
    public List<LichLamViecDto> saveWeek(LichTuanRequest request) {
        LocalDate weekStart = requireMonday(request.getTuNgay());
        LocalDate weekEnd = weekStart.plusDays(6);

        Set<String> seen = new HashSet<>();
        for (LichLamViecKhoaRequest cell : request.getXoa()) {
            checkInWeek(cell.getNgay(), weekStart, weekEnd);
            checkUnique(seen, cell.getMaBacSi(), cell.getNgay());
        }
        for (LichLamViecRequest cell : request.getLuu()) {
            checkInWeek(cell.getNgay(), weekStart, weekEnd);
            checkUnique(seen, cell.getMaBacSi(), cell.getNgay());
            checkHours(cell);
        }

        Map<Integer, BacSi> doctors = new HashMap<>();
        for (LichLamViecRequest cell : request.getLuu()) {
            upsert(cell, doctors);
        }
        for (LichLamViecKhoaRequest cell : request.getXoa()) {
            remove(cell);
        }
        return toDtos(lichLamViecRepository.findInRange(weekStart, weekEnd, null));
    }

    // BR-13: chép các buổi lam_viec của tuần trước sang tuần đích (+7 ngày), bỏ qua ngày đã có dòng
    @Transactional
    public SaoChepTuanDto copyPreviousWeek(SaoChepTuanRequest request) {
        return copyIntoWeek(request.getTuNgay());
    }

    // targetStart là thứ Hai của tuần đích; dùng chung cho API và job định kỳ (BR-14)
    @Transactional
    public SaoChepTuanDto copyIntoWeek(LocalDate targetWeekStart) {
        LocalDate targetStart = requireMonday(targetWeekStart);
        LocalDate targetEnd = targetStart.plusDays(6);

        Set<String> existing = new HashSet<>();
        for (LichLamViec schedule : lichLamViecRepository.findInRange(targetStart, targetEnd, null)) {
            existing.add(schedule.getBacSi().getMaBacSi() + "|" + schedule.getNgay());
        }

        int copied = 0;
        int skipped = 0;
        for (LichLamViec source : lichLamViecRepository.findInRange(targetStart.minusDays(7), targetStart.minusDays(1), null)) {
            if (source.getTrangThai() != TrangThaiLich.lam_viec) {
                continue;
            }
            LocalDate targetDate = source.getNgay().plusDays(7);
            boolean doctorActive = source.getBacSi().getTrangThaiLamViec() == TrangThaiLamViec.dang_lam;
            if (!doctorActive || existing.contains(source.getBacSi().getMaBacSi() + "|" + targetDate)) {
                skipped++;
                continue;
            }
            // Không chép ghi chú vì nó gắn với đúng ngày cũ
            LichLamViec copy = new LichLamViec();
            copy.setBacSi(source.getBacSi());
            copy.setNgay(targetDate);
            copy.setGioBatDau(source.getGioBatDau());
            copy.setGioKetThuc(source.getGioKetThuc());
            copy.setTrangThai(TrangThaiLich.lam_viec);
            lichLamViecRepository.save(copy);
            copied++;
        }
        return new SaoChepTuanDto(copied, skipped,
                toDtos(lichLamViecRepository.findInRange(targetStart, targetEnd, null)));
    }

    // BR-15: chuyển nghỉ hoặc đổi giờ một buổi; lịch hẹn không còn nằm trong buổi được trả về để nhân viên
    // hủy và thông báo. Hệ thống không tự hủy lịch hẹn.
    @Transactional
    public DoiLichLamViecDto update(Integer scheduleId, LichLamViecCapNhatRequest request) {
        if (request.getGioBatDau() == null && request.getGioKetThuc() == null
                && request.getTrangThai() == null && request.getGhiChu() == null) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE, "Không có thông tin nào để cập nhật");
        }
        LichLamViec schedule = lichLamViecRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ResponseCode.SCHEDULE_NOT_FOUND));

        LocalTime start = request.getGioBatDau() != null ? request.getGioBatDau() : schedule.getGioBatDau();
        LocalTime end = request.getGioKetThuc() != null ? request.getGioKetThuc() : schedule.getGioKetThuc();
        checkHours(start, end);
        schedule.setGioBatDau(start);
        schedule.setGioKetThuc(end);
        if (request.getTrangThai() != null) {
            schedule.setTrangThai(request.getTrangThai());
        }
        if (request.getGhiChu() != null) {
            schedule.setGhiChu(blankToNull(request.getGhiChu()));
        }

        // Chỉ lịch chưa khám xong mới cần xử lý; nghỉ thì mọi lịch bị ảnh hưởng, còn lại là lịch ngoài giờ mới
        List<LichHenAnhHuongDto> affected = lichHenRepository
                .findByLichLamViec_MaLichAndTrangThaiInOrderByGioHen(scheduleId, PENDING_STATUSES).stream()
                .filter(appointment -> schedule.getTrangThai() == TrangThaiLich.nghi
                        || appointment.getGioHen().isBefore(start)
                        || appointment.getGioHen().plusMinutes(clinicProperties.getSlotMinutes()).isAfter(end))
                .map(LichHenAnhHuongDto::of)
                .toList();
        lichLamViecRepository.save(schedule);
        long activeCount = lichHenRepository.countByLichLamViec_MaLichAndTrangThaiNot(
                scheduleId, TrangThaiLichHen.da_huy);
        return new DoiLichLamViecDto(LichLamViecDto.of(schedule, activeCount), affected);
    }

    private static LocalDate requireMonday(LocalDate date) {
        if (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE, "Ngày bắt đầu tuần phải là thứ Hai");
        }
        return date;
    }

    private void upsert(LichLamViecRequest cell, Map<Integer, BacSi> doctors) {
        BacSi doctor = doctors.computeIfAbsent(cell.getMaBacSi(), id -> bacSiRepository.findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.DOCTOR_NOT_FOUND)));
        if (doctor.getTrangThaiLamViec() != TrangThaiLamViec.dang_lam) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE,
                    "Bác sĩ " + doctor.getHoTen() + " đã nghỉ việc, không sắp lịch được");
        }
        String note = blankToNull(cell.getGhiChu());
        LichLamViec schedule = lichLamViecRepository
                .findByBacSi_MaBacSiAndNgay(cell.getMaBacSi(), cell.getNgay()).orElse(null);
        if (schedule == null) {
            schedule = new LichLamViec();
            schedule.setBacSi(doctor);
            schedule.setNgay(cell.getNgay());
        } else if (isUnchanged(schedule, cell, note)) {
            return;
        } else if (lichHenRepository.countByLichLamViec_MaLichAndTrangThaiNot(
                schedule.getMaLich(), TrangThaiLichHen.da_huy) > 0) {
            // Ô đã có lịch hẹn: phải qua luồng chuyển nghỉ hoặc rút giờ để xử lý lịch hẹn (BR-15)
            throw new AppException(ResponseCode.SCHEDULE_HAS_APPOINTMENTS, "Ngày " + cell.getNgay()
                    + " của bác sĩ " + doctor.getHoTen() + " đã có lịch hẹn, không sửa trực tiếp được");
        }
        schedule.setGioBatDau(cell.getGioBatDau());
        schedule.setGioKetThuc(cell.getGioKetThuc());
        schedule.setTrangThai(cell.getTrangThai());
        schedule.setGhiChu(note);
        lichLamViecRepository.save(schedule);
    }

    private void remove(LichLamViecKhoaRequest cell) {
        lichLamViecRepository.findByBacSi_MaBacSiAndNgay(cell.getMaBacSi(), cell.getNgay()).ifPresent(schedule -> {
            // BR-16: khóa ngoại chặn xóa buổi có lịch hẹn (kể cả lịch đã hủy)
            if (lichHenRepository.countByLichLamViec_MaLich(schedule.getMaLich()) > 0) {
                throw new AppException(ResponseCode.SCHEDULE_HAS_APPOINTMENTS,
                        "Ngày " + cell.getNgay() + " đã có lịch hẹn, không xóa được; hãy chuyển sang nghỉ");
            }
            lichLamViecRepository.delete(schedule);
        });
    }

    private List<LichLamViecDto> toDtos(List<LichLamViec> schedules) {
        Map<Integer, Long> activeCounts = new HashMap<>();
        if (!schedules.isEmpty()) {
            List<Integer> ids = schedules.stream().map(LichLamViec::getMaLich).toList();
            for (Object[] row : lichHenRepository.countActiveBySchedule(ids, TrangThaiLichHen.da_huy)) {
                activeCounts.put((Integer) row[0], (Long) row[1]);
            }
        }
        return schedules.stream()
                .map(schedule -> LichLamViecDto.of(schedule, activeCounts.getOrDefault(schedule.getMaLich(), 0L)))
                .toList();
    }

    private static boolean isUnchanged(LichLamViec schedule, LichLamViecRequest cell, String note) {
        return schedule.getGioBatDau().equals(cell.getGioBatDau())
                && schedule.getGioKetThuc().equals(cell.getGioKetThuc())
                && schedule.getTrangThai() == cell.getTrangThai()
                && Objects.equals(schedule.getGhiChu(), note);
    }

    private static void checkInWeek(LocalDate date, LocalDate weekStart, LocalDate weekEnd) {
        if (date.isBefore(weekStart) || date.isAfter(weekEnd)) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE, "Ngày " + date + " nằm ngoài tuần đang lưu");
        }
    }

    private static void checkUnique(Set<String> seen, Integer doctorId, LocalDate date) {
        if (!seen.add(doctorId + "|" + date)) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE,
                    "Bác sĩ " + doctorId + " bị lặp ngày " + date + " trong cùng một lần lưu");
        }
    }

    // BR-11: giờ tròn :00 hoặc :30, kết thúc sau bắt đầu
    private static void checkHours(LichLamViecRequest cell) {
        checkHours(cell.getGioBatDau(), cell.getGioKetThuc());
    }

    private static void checkHours(LocalTime start, LocalTime end) {
        if (!isHalfHour(start) || !isHalfHour(end)) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE, "Giờ phải tròn :00 hoặc :30");
        }
        if (!end.isAfter(start)) {
            throw new AppException(ResponseCode.INVALID_SCHEDULE, "Giờ kết thúc phải sau giờ bắt đầu");
        }
    }

    private static boolean isHalfHour(LocalTime time) {
        return (time.getMinute() == 0 || time.getMinute() == 30) && time.getSecond() == 0 && time.getNano() == 0;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }
}
