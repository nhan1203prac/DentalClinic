package com.dentalclinic.lichlamviec.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaoChepTuanDto {
    // Số ô đã chép mới
    private final int daChep;
    // Số ô tuần trước không chép: ngày đích đã có lịch, hoặc bác sĩ đã nghỉ việc
    private final int boQua;
    // Toàn bộ lịch của tuần đích sau khi chép
    private final List<LichLamViecDto> lich;
}
