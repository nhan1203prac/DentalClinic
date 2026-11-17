package com.dentalclinic.taikhoan.dto;

import java.time.LocalDate;
import com.dentalclinic.taikhoan.entity.NhanVien;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NhanVienDto {
    private final Integer maNhanVien;
    private final Integer maTaiKhoan;
    private final String tenDangNhap;
    private final String hoTen;
    private final String soDienThoai;
    private final String email;
    private final LocalDate ngayVaoLam;
    private final TrangThaiLamViec trangThaiLamViec;
    private final TrangThaiTaiKhoan trangThaiTaiKhoan;

    public static NhanVienDto of(NhanVien staff) {
        TaiKhoan account = staff.getTaiKhoan();
        return new NhanVienDto(staff.getMaNhanVien(), account.getMaTaiKhoan(), account.getTenDangNhap(),
                staff.getHoTen(), account.getSoDienThoai(), account.getEmail(), staff.getNgayVaoLam(),
                staff.getTrangThaiLamViec(), account.getTrangThai());
    }
}
