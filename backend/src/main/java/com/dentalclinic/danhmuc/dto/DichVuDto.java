package com.dentalclinic.danhmuc.dto;

import java.math.BigDecimal;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DichVuDto {
    private final Integer maDichVu;
    private final String tenDichVu;
    private final String moTa;
    private final BigDecimal gia;
    private final Integer thoiLuong;
    private final TrangThaiDichVu trangThai;

    public static DichVuDto of(DichVu service) {
        return new DichVuDto(service.getMaDichVu(), service.getTenDichVu(), service.getMoTa(), service.getGia(),
                service.getThoiLuong(), service.getTrangThai());
    }
}
