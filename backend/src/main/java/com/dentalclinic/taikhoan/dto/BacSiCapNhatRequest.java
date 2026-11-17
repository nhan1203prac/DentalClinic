package com.dentalclinic.taikhoan.dto;

import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BacSiCapNhatRequest extends LienHeRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @Size(max = 100, message = "Chuyên môn tối đa 100 ký tự")
    private String chuyenMon;

    @Size(max = 255, message = "Bằng cấp tối đa 255 ký tự")
    private String bangCap;

    private String gioiThieu;

    @NotNull(message = "Trạng thái làm việc không được để trống")
    private TrangThaiLamViec trangThaiLamViec;
}
