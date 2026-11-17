package com.dentalclinic.taikhoan.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tạo nhân viên: tài khoản và hồ sơ cùng lúc (BR-04)
@Getter
@Setter
@NoArgsConstructor
public class NhanVienRequest extends TaiKhoanMoiRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    private LocalDate ngayVaoLam;
}
