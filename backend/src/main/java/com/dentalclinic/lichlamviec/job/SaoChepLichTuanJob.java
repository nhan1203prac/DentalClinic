package com.dentalclinic.lichlamviec.job;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.dentalclinic.lichlamviec.dto.SaoChepTuanDto;
import com.dentalclinic.lichlamviec.service.LichLamViecService;

// BR-14 (tùy chọn): Chủ nhật 20:00 tự sao chép lịch tuần hiện tại sang tuần sau.
// Tắt bằng app.schedule.auto-copy.enabled=false; đổi giờ chạy bằng app.schedule.auto-copy.cron.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.schedule.auto-copy.enabled", havingValue = "true", matchIfMissing = true)
public class SaoChepLichTuanJob {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final LichLamViecService lichLamViecService;

    @Scheduled(cron = "${app.schedule.auto-copy.cron:0 0 20 * * SUN}", zone = "Asia/Ho_Chi_Minh")
    public void run() {
        try {
            SaoChepTuanDto result = copyNextWeek(LocalDate.now(ZONE));
            log.info("Tự động sao chép lịch tuần: đã chép {} buổi, bỏ qua {} buổi", result.getDaChep(), result.getBoQua());
        } catch (RuntimeException e) {
            // Job lỗi không được làm sập ứng dụng; chủ phòng khám vẫn sao chép tay được
            log.error("Tự động sao chép lịch tuần thất bại", e);
        }
    }

    // Tuần đích là tuần bắt đầu từ thứ Hai kế tiếp; nguồn là tuần liền trước nó (tức tuần hiện tại)
    SaoChepTuanDto copyNextWeek(LocalDate today) {
        return lichLamViecService.copyIntoWeek(today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
    }
}
