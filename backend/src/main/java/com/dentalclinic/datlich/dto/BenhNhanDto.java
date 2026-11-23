package com.dentalclinic.datlich.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Người khám của một tài khoản, kèm tiền sử để màn đặt lịch hiện sẵn
@Getter
@AllArgsConstructor
public class BenhNhanDto {
    private final Integer maBenhNhan;
    private final String hoTen;
    private final LocalDate ngaySinh;
    private final String gioiTinh;
    private final String diaChi;
    private final List<TienSuDto> tienSu;

    @Getter
    @AllArgsConstructor
    public static class TienSuDto {
        private final String loaiTienSu;
        private final String tenTienSu;
        private final String ghiChu;
    }
}
