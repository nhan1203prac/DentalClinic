package com.dentalclinic.lichlamviec.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LichLamViecDto {
    private final Integer maLich;
    private final Integer maBacSi;
    private final LocalDate ngay;
    private final LocalTime gioBatDau;
    private final LocalTime gioKetThuc;
    private final TrangThaiLich trangThai;
    private final String ghiChu;
    // Số lịch hẹn còn hiệu lực; > 0 thì ô không sửa hay xóa trực tiếp được (BR-15, BR-16)
    private final long soLichHen;

    public static LichLamViecDto of(LichLamViec schedule, long activeAppointments) {
        return new LichLamViecDto(schedule.getMaLich(), schedule.getBacSi().getMaBacSi(), schedule.getNgay(),
                schedule.getGioBatDau(), schedule.getGioKetThuc(), schedule.getTrangThai(), schedule.getGhiChu(),
                activeAppointments);
    }
}
