package com.dentalclinic.danhmuc.dto;

import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Chủ phòng khám sửa thông tin và có thể ngưng dịch vụ (trangThai = ngung)
@Getter
@Setter
@NoArgsConstructor
public class DichVuCapNhatRequest extends DichVuRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private TrangThaiDichVu trangThai;
}
