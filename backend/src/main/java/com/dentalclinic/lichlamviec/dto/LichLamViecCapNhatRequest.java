package com.dentalclinic.lichlamviec.dto;

import java.time.LocalTime;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Chuyển nghỉ hoặc đổi giờ một buổi đã có. Trường để trống (null) = giữ nguyên; ghiChu = "" để xóa ghi chú.
@Getter
@Setter
@NoArgsConstructor
public class LichLamViecCapNhatRequest {

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private TrangThaiLich trangThai;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String ghiChu;
}
