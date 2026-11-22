package com.dentalclinic.lichlamviec.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Khóa tự nhiên của một ô lịch: (bác sĩ, ngày)
@Getter
@Setter
@NoArgsConstructor
public class LichLamViecKhoaRequest {

    @NotNull(message = "Chưa chọn bác sĩ")
    private Integer maBacSi;

    @NotNull(message = "Chưa chọn ngày")
    private LocalDate ngay;
}
