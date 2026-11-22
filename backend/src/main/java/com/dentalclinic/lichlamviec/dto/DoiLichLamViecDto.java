package com.dentalclinic.lichlamviec.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoiLichLamViecDto {
    private final LichLamViecDto lich;
    // Rỗng nghĩa là đổi lịch không ảnh hưởng ai
    private final List<LichHenAnhHuongDto> lichHenBiAnhHuong;
}
