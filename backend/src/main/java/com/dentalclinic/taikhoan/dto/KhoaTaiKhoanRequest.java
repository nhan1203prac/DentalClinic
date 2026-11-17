package com.dentalclinic.taikhoan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KhoaTaiKhoanRequest {

    @NotNull(message = "Thiếu trạng thái khóa")
    private Boolean khoa;
}
