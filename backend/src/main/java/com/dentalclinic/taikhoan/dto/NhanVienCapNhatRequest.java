package com.dentalclinic.taikhoan.dto;

import java.time.LocalDate;
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
public class NhanVienCapNhatRequest extends LienHeRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    private LocalDate ngayVaoLam;

    @NotNull(message = "Trạng thái làm việc không được để trống")
    private TrangThaiLamViec trangThaiLamViec;
}
