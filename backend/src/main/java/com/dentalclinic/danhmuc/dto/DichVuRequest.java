package com.dentalclinic.danhmuc.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DichVuRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 150, message = "Tên dịch vụ tối đa 150 ký tự")
    private String tenDichVu;

    private String moTa;

    @NotNull(message = "Giá không được để trống")
    @PositiveOrZero(message = "Giá không được âm")
    @Digits(integer = 12, fraction = 0, message = "Giá phải là số nguyên VNĐ")
    private BigDecimal gia;

    @Positive(message = "Thời lượng phải lớn hơn 0")
    private Integer thoiLuong;
}
