package com.dentalclinic.datlich.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.entity.ChiTietLichHen;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.ChiTietLichHenRepository;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.security.UserPrincipal;

// Lịch khám của bệnh nhân: xem và tự hủy (BR-26)
@Service
@RequiredArgsConstructor
public class LichKhamService {
    private final LichHenRepository lichHenRepository;
    private final ChiTietLichHenRepository chiTietLichHenRepository;
    private final Clock clock;

    // Lịch của mọi người khám thuộc tài khoản, mới nhất trước
    @Transactional(readOnly = true)
    public List<LichHenDto> myAppointments(UserPrincipal user) {
        List<LichHen> appointments = lichHenRepository.findByAccount(user.getMaTaiKhoan());
        if (appointments.isEmpty()) {
            return List.of();
        }
        Map<Integer, List<String>> services = new HashMap<>();
        for (ChiTietLichHen detail : chiTietLichHenRepository.findWithServiceByAppointments(
                appointments.stream().map(LichHen::getMaLichHen).toList())) {
            services.computeIfAbsent(detail.getLichHen().getMaLichHen(), id -> new java.util.ArrayList<>())
                    .add(detail.getDichVu().getTenDichVu());
        }
        return appointments.stream()
                .map(a -> LichHenDto.from(a, services.getOrDefault(a.getMaLichHen(), List.of()), canCancel(a)))
                .toList();
    }

    // BR-26: chỉ hủy lịch của chính mình, đang da_dat, trước giờ khám. Giữ nguyên dòng, chỉ đổi trạng thái
    // (cột sinh con_hieu_luc về NULL nên khung được giải phóng, BR-21).
    @Transactional
    public LichHenDto cancel(Integer appointmentId, UserPrincipal user) {
        // Lịch của người khác cũng báo "không tìm thấy" để không lộ lịch nào tồn tại
        LichHen appointment = lichHenRepository.findDetailById(appointmentId)
                .filter(a -> a.getBenhNhan().getTaiKhoan().getMaTaiKhoan().equals(user.getMaTaiKhoan()))
                .orElseThrow(() -> new AppException(ResponseCode.APPOINTMENT_NOT_FOUND));
        if (!canCancel(appointment)) {
            throw new AppException(ResponseCode.APPOINTMENT_NOT_CANCELLABLE);
        }
        appointment.setTrangThai(TrangThaiLichHen.da_huy);
        List<String> services = chiTietLichHenRepository.findWithServiceByAppointments(List.of(appointmentId)).stream()
                .map(d -> d.getDichVu().getTenDichVu()).toList();
        return LichHenDto.from(appointment, services, false);
    }

    // Cần appointment đã nạp sẵn buổi làm việc
    public boolean canCancel(LichHen appointment) {
        LocalDateTime start = LocalDateTime.of(appointment.getLichLamViec().getNgay(), appointment.getGioHen());
        return appointment.getTrangThai() == TrangThaiLichHen.da_dat && start.isAfter(LocalDateTime.now(clock));
    }
}
