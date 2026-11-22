package com.dentalclinic.taikhoan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tạo bác sĩ: tài khoản và hồ sơ cùng lúc (BR-04)
@Getter
@Setter
@NoArgsConstructor
public class BacSiRequest extends TaiKhoanMoiRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @Size(max = 100, message = "Chuyên môn tối đa 100 ký tự")
    private String chuyenMon;

    @Size(max = 255, message = "Bằng cấp tối đa 255 ký tự")
    private String bangCap;

    private String gioiThieu;

    // Link ảnh Cloudinary do frontend tải lên; để trống = không có ảnh
    @Size(max = 255, message = "Link ảnh tối đa 255 ký tự")
    private String anhDaiDien;
}
