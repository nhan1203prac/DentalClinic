package com.dentalclinic.taikhoan.dto;

import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BacSiDto {
    private final Integer maBacSi;
    private final Integer maTaiKhoan;
    private final String tenDangNhap;
    private final String hoTen;
    private final String soDienThoai;
    private final String email;
    private final String chuyenMon;
    private final String bangCap;
    private final String gioiThieu;
    private final String anhDaiDien;
    private final TrangThaiLamViec trangThaiLamViec;
    private final TrangThaiTaiKhoan trangThaiTaiKhoan;

    public static BacSiDto of(BacSi doctor) {
        TaiKhoan account = doctor.getTaiKhoan();
        return new BacSiDto(doctor.getMaBacSi(), account.getMaTaiKhoan(), account.getTenDangNhap(),
                doctor.getHoTen(), account.getSoDienThoai(), account.getEmail(), doctor.getChuyenMon(),
                doctor.getBangCap(), doctor.getGioiThieu(), doctor.getAnhDaiDien(), doctor.getTrangThaiLamViec(),
                account.getTrangThai());
    }
}
