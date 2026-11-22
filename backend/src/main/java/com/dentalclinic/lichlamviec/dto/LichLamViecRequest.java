package com.dentalclinic.lichlamviec.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Một ô của lưới tuần: bác sĩ + ngày (BR-10)
@Getter
@Setter
@NoArgsConstructor
public class LichLamViecRequest {

    @NotNull(message = "Chưa chọn bác sĩ")
    private Integer maBacSi;

    @NotNull(message = "Chưa chọn ngày")
    private LocalDate ngay;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime gioKetThuc;

    @NotNull(message = "Trạng thái không được để trống")
    private TrangThaiLich trangThai;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String ghiChu;
}
