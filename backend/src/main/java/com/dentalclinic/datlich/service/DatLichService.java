package com.dentalclinic.datlich.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.benhnhan.entity.TienSuBenh;
import com.dentalclinic.benhnhan.repository.BenhNhanRepository;
import com.dentalclinic.benhnhan.repository.TienSuBenhRepository;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ClinicProperties;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;
import com.dentalclinic.danhmuc.repository.DichVuRepository;
import com.dentalclinic.datlich.dto.BenhNhanDto;
import com.dentalclinic.datlich.dto.BenhNhanMoiRequest;
import com.dentalclinic.datlich.dto.DatLichRequest;
import com.dentalclinic.datlich.dto.KhungGioDto;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.dto.TienSuRequest;
import com.dentalclinic.datlich.entity.ChiTietLichHen;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.ChiTietLichHenRepository;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.repository.LichLamViecRepository;
import com.dentalclinic.security.UserPrincipal;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.repository.TaiKhoanRepository;

@Service
@RequiredArgsConstructor
public class DatLichService {
    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;
    private final ChiTietLichHenRepository chiTietLichHenRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final TienSuBenhRepository tienSuBenhRepository;
    private final DichVuRepository dichVuRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhungTrongService khungTrongService;
    private final ClinicProperties clinic;
    private final Clock clock;

    // Người khám của tài khoản đang đăng nhập, kèm tiền sử
    @Transactional(readOnly = true)
    public List<BenhNhanDto> myPatients(UserPrincipal user) {
        List<BenhNhan> patients = benhNhanRepository
                .findByTaiKhoan_MaTaiKhoanOrderByMaBenhNhan(user.getMaTaiKhoan());
        if (patients.isEmpty()) {
            return List.of();
        }
        List<TienSuBenh> histories = tienSuBenhRepository.findByBenhNhan_MaBenhNhanInOrderByMaTienSu(
                patients.stream().map(BenhNhan::getMaBenhNhan).toList());
        return patients.stream()
                .map(p -> new BenhNhanDto(p.getMaBenhNhan(), p.getHoTen(), p.getNgaySinh(), p.getGioiTinh(),
                        p.getDiaChi(),
                        histories.stream()
                                .filter(h -> h.getBenhNhan().getMaBenhNhan().equals(p.getMaBenhNhan()))
                                .map(h -> new BenhNhanDto.TienSuDto(h.getLoaiTienSu().name(), h.getTenTienSu(),
                                        h.getGhiChu()))
                                .toList()))
                .toList();
    }

    // BR-24: toàn bộ việc đặt lịch trong một transaction, lỗi ở bước nào thì không lưu gì
    @Transactional
    public LichHenDto book(DatLichRequest request, UserPrincipal user) {
        if ((request.getMaBenhNhan() == null) == (request.getBenhNhanMoi() == null)) {
            throw new AppException(ResponseCode.BAD_REQUEST, "Hãy chọn người khám có sẵn hoặc nhập người khám mới");
        }
        LichLamViec schedule = lichLamViecRepository.findById(request.getMaLich())
                .orElseThrow(() -> new AppException(ResponseCode.SCHEDULE_NOT_FOUND));
        checkBookable(schedule, request);

        BenhNhan patient = request.getMaBenhNhan() != null
                ? findOwnPatient(request.getMaBenhNhan(), user)
                : createPatient(request.getBenhNhanMoi(), user);
        if (lichHenRepository.countActiveAt(patient.getMaBenhNhan(), schedule.getNgay(), request.getGioHen(),
                TrangThaiLichHen.da_huy) > 0) {
            throw new AppException(ResponseCode.PATIENT_TIME_CONFLICT);
        }
        List<DichVu> services = findServices(request.getDichVuDangKy());
        addHistory(patient, request.getTienSu());

        LichHen appointment = new LichHen();
        appointment.setBenhNhan(patient);
        appointment.setLichLamViec(schedule);
        appointment.setGioHen(request.getGioHen());
        appointment.setLyDoKham(blankToNull(request.getLyDoKham()));
        try {
            // flush ngay để UQ_LICH_HEN_lich_gio báo lỗi tại đây (BR-21)
            lichHenRepository.saveAndFlush(appointment);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ResponseCode.SLOT_TAKEN);
        }
        for (DichVu service : services) {
            ChiTietLichHen detail = new ChiTietLichHen();
            detail.setLichHen(appointment);
            detail.setDichVu(service);
            chiTietLichHenRepository.save(detail);
        }
        return LichHenDto.from(appointment, services.stream().map(DichVu::getTenDichVu).toList(), true);
    }

    // BR-25, BR-22: ngày trong khoảng cho phép; giờ phải là một khung còn đặt được của buổi làm việc
    private void checkBookable(LichLamViec schedule, DatLichRequest request) {
        LocalDate today = LocalDate.now(clock);
        LocalDate last = today.plusDays(clinic.getMaxBookingDays() - 1L);
        if (schedule.getNgay().isBefore(today) || schedule.getNgay().isAfter(last)) {
            throw new AppException(ResponseCode.BOOKING_OUT_OF_RANGE);
        }
        if (schedule.getBacSi().getTrangThaiLamViec() != TrangThaiLamViec.dang_lam) {
            throw new AppException(ResponseCode.INVALID_SLOT);
        }
        KhungGioDto slot = khungTrongService.slotsOf(schedule).stream()
                .filter(s -> s.getGio().equals(request.getGioHen()))
                .findFirst()
                .orElseThrow(() -> new AppException(ResponseCode.INVALID_SLOT));
        if (!slot.isTrong()) {
            throw new AppException(ResponseCode.SLOT_TAKEN);
        }
    }

    // Hồ sơ của người khác cũng báo "không tìm thấy" để không lộ hồ sơ nào tồn tại
    private BenhNhan findOwnPatient(Integer patientId, UserPrincipal user) {
        return benhNhanRepository.findById(patientId)
                .filter(p -> p.getTaiKhoan().getMaTaiKhoan().equals(user.getMaTaiKhoan()))
                .orElseThrow(() -> new AppException(ResponseCode.PATIENT_NOT_FOUND));
    }

    // BR-03: hồ sơ mới chỉ tạo khi đặt lịch, không trùng (tài khoản, họ tên, ngày sinh)
    private BenhNhan createPatient(BenhNhanMoiRequest input, UserPrincipal user) {
        String name = input.getHoTen().trim();
        if (benhNhanRepository.existsByTaiKhoan_MaTaiKhoanAndHoTenAndNgaySinh(user.getMaTaiKhoan(), name,
                input.getNgaySinh())) {
            throw new AppException(ResponseCode.PATIENT_DUPLICATE);
        }
        BenhNhan patient = new BenhNhan();
        patient.setTaiKhoan(taiKhoanRepository.getReferenceById(user.getMaTaiKhoan()));
        patient.setHoTen(name);
        patient.setNgaySinh(input.getNgaySinh());
        patient.setGioiTinh(blankToNull(input.getGioiTinh()));
        patient.setDiaChi(blankToNull(input.getDiaChi()));
        try {
            return benhNhanRepository.saveAndFlush(patient);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ResponseCode.PATIENT_DUPLICATE);
        }
    }

    // BR-24: dịch vụ không bắt buộc; chỉ nhận dịch vụ còn áp dụng, bỏ id trùng
    private List<DichVu> findServices(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Integer> distinct = ids.stream().distinct().toList();
        List<DichVu> services = dichVuRepository.findAllById(distinct).stream()
                .filter(s -> s.getTrangThai() == TrangThaiDichVu.kich_hoat)
                .toList();
        if (services.size() != distinct.size()) {
            throw new AppException(ResponseCode.SERVICE_NOT_FOUND);
        }
        return services;
    }

    // Tiền sử chỉ thêm mới, không sửa hay xóa mục cũ
    private void addHistory(BenhNhan patient, List<TienSuRequest> items) {
        if (items == null) {
            return;
        }
        for (TienSuRequest item : items) {
            TienSuBenh history = new TienSuBenh();
            history.setBenhNhan(patient);
            history.setLoaiTienSu(item.getLoaiTienSu());
            history.setTenTienSu(item.getTenTienSu().trim());
            history.setGhiChu(blankToNull(item.getGhiChu()));
            tienSuBenhRepository.save(history);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
