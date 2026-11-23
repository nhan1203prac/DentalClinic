package com.dentalclinic.datlich.dto;

import com.dentalclinic.benhnhan.entity.LoaiTienSu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TienSuRequest {
    @NotNull(message = "Chưa chọn loại tiền sử")
    private LoaiTienSu loaiTienSu;

    @NotBlank(message = "Tên tiền sử không được để trống")
    @Size(max = 150, message = "Tên tiền sử tối đa 150 ký tự")
    private String tenTienSu;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String ghiChu;
}
