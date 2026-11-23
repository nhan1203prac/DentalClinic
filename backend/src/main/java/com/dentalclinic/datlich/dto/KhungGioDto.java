package com.dentalclinic.datlich.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Một khung khám: gio là giờ bắt đầu, trong = false khi đã có lịch hẹn còn hiệu lực
@Getter
@AllArgsConstructor
public class KhungGioDto {
    private final LocalTime gio;
    private final boolean trong;
}
