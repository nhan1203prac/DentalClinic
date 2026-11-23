package com.dentalclinic.datlich.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.dentalclinic.datlich.entity.LichHen;

@Getter
@AllArgsConstructor
public class LichHenDto {
    private final Integer maLichHen;
    private final Integer maBenhNhan;
    private final String tenBenhNhan;
    private final Integer maBacSi;
    private final String tenBacSi;
    private final LocalDate ngay;
    private final LocalTime gioHen;
    private final String trangThai;
    private final String lyDoKham;
    private final List<String> dichVu;
    // BR-26: bệnh nhân còn hủy được (đang da_dat và chưa tới giờ hẹn)
    private final boolean coTheHuy;

    // Cần lichHen đã nạp sẵn người khám, buổi làm việc và bác sĩ
    public static LichHenDto from(LichHen appointment, List<String> services, boolean cancellable) {
        return new LichHenDto(appointment.getMaLichHen(), appointment.getBenhNhan().getMaBenhNhan(),
                appointment.getBenhNhan().getHoTen(), appointment.getLichLamViec().getBacSi().getMaBacSi(),
                appointment.getLichLamViec().getBacSi().getHoTen(), appointment.getLichLamViec().getNgay(),
                appointment.getGioHen(), appointment.getTrangThai().name(), appointment.getLyDoKham(), services,
                cancellable);
    }
}
