package com.dentalclinic.lichlamviec.dto;

import java.time.LocalTime;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Lịch hẹn không còn nằm trong buổi làm việc sau khi đổi; nhân viên dùng để liên hệ và hủy
@Getter
@AllArgsConstructor
public class LichHenAnhHuongDto {
    private final Integer maLichHen;
    private final LocalTime gioHen;
    private final TrangThaiLichHen trangThai;
    private final String lyDoKham;
    private final Integer maBenhNhan;
    private final String hoTenBenhNhan;
    // Số điện thoại nằm ở tài khoản đặt lịch (BR-02)
    private final String soDienThoai;

    public static LichHenAnhHuongDto of(LichHen appointment) {
        return new LichHenAnhHuongDto(appointment.getMaLichHen(), appointment.getGioHen(),
                appointment.getTrangThai(), appointment.getLyDoKham(), appointment.getBenhNhan().getMaBenhNhan(),
                appointment.getBenhNhan().getHoTen(), appointment.getBenhNhan().getTaiKhoan().getSoDienThoai());
    }
}
