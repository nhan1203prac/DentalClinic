package com.dentalclinic.datlich.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// BR-03: hồ sơ người khám mới, chỉ tạo khi đặt lịch
@Getter
@Setter
@NoArgsConstructor
public class BenhNhanMoiRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải trước hôm nay")
    private LocalDate ngaySinh;

    @Size(max = 10, message = "Giới tính tối đa 10 ký tự")
    private String gioiTinh;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String diaChi;
}
