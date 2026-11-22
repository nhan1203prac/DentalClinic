package com.dentalclinic.lichlamviec.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// tuNgay là thứ Hai của tuần ĐÍCH; nguồn luôn là tuần liền trước
@Getter
@Setter
@NoArgsConstructor
public class SaoChepTuanRequest {

    @NotNull(message = "Chưa chọn tuần")
    private LocalDate tuNgay;
}
