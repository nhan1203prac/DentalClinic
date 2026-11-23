package com.dentalclinic.common;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// BR-12: quy ước chung của phòng khám, cấu hình ở application.properties (app.clinic.*)
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.clinic")
public class ClinicProperties {
    // Độ dài một khung khám
    private int slotMinutes = 30;
    // Nghỉ trưa chung, không nhận đặt lịch trong khoảng [lunchStart, lunchEnd)
    private LocalTime lunchStart = LocalTime.of(11, 30);
    private LocalTime lunchEnd = LocalTime.of(13, 30);
    // BR-25: chỉ đặt lịch trong số ngày tới này (tính cả hôm nay)
    private int maxBookingDays = 14;
}
