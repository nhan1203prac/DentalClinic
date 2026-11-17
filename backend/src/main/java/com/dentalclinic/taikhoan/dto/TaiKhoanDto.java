package com.dentalclinic.taikhoan.dto;

import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;
import com.dentalclinic.taikhoan.entity.VaiTro;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaiKhoanDto {
    private final Integer maTaiKhoan;
    private final String tenDangNhap;
    private final String soDienThoai;
    private final String email;
    private final VaiTro vaiTro;
    private final TrangThaiTaiKhoan trangThai;

    public static TaiKhoanDto of(TaiKhoan account) {
        return new TaiKhoanDto(account.getMaTaiKhoan(), account.getTenDangNhap(), account.getSoDienThoai(),
                account.getEmail(), account.getVaiTro(), account.getTrangThai());
    }
}
