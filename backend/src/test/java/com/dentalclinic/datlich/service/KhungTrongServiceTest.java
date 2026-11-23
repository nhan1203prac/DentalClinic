package com.dentalclinic.datlich.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.dentalclinic.common.ClinicProperties;
import com.dentalclinic.datlich.dto.KhungGioDto;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import com.dentalclinic.lichlamviec.repository.LichLamViecRepository;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;

class KhungTrongServiceTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    // "Hôm nay" trong test là thứ Hai 2026-11-23, lúc 09:10
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private LichLamViecRepository lichLamViecRepository;
    private LichHenRepository lichHenRepository;
    private KhungTrongService service;

    @BeforeEach
    void setUp() {
        lichLamViecRepository = mock(LichLamViecRepository.class);
        lichHenRepository = mock(LichHenRepository.class);
        Clock clock = Clock.fixed(LocalDateTime.of(TODAY, LocalTime.of(9, 10)).atZone(ZONE).toInstant(), ZONE);
        service = new KhungTrongService(lichLamViecRepository, lichHenRepository, new ClinicProperties(), clock);
        when(lichHenRepository.findBookedTimes(any(), any())).thenReturn(List.of());
    }

    private static LichLamViec schedule(LocalDate date, String start, String end, TrangThaiLich status) {
        BacSi doctor = new BacSi();
        doctor.setMaBacSi(1);
        doctor.setTrangThaiLamViec(TrangThaiLamViec.dang_lam);
        LichLamViec schedule = new LichLamViec();
        schedule.setMaLich(10);
        schedule.setBacSi(doctor);
        schedule.setNgay(date);
        schedule.setGioBatDau(LocalTime.parse(start));
        schedule.setGioKetThuc(LocalTime.parse(end));
        schedule.setTrangThai(status);
        return schedule;
    }

    private static List<String> times(List<KhungGioDto> slots) {
        return slots.stream().map(slot -> slot.getGio().toString()).toList();
    }

    @Test
    void cutsFullDayIntoHalfHourSlotsAndSkipsLunchBreak() {
        List<LocalTime> slots = service.cutSlots(LocalTime.of(8, 0), LocalTime.of(17, 0));

        // 08:00–11:30 (7 khung) + 13:30–17:00 (7 khung); 11:30, 12:00, 12:30, 13:00 là giờ nghỉ trưa
        assertEquals(14, slots.size());
        assertEquals(LocalTime.of(8, 0), slots.get(0));
        assertTrue(slots.contains(LocalTime.of(11, 0)));
        assertFalse(slots.contains(LocalTime.of(11, 30)));
        assertFalse(slots.contains(LocalTime.of(13, 0)));
        assertTrue(slots.contains(LocalTime.of(13, 30)));
        assertEquals(LocalTime.of(16, 30), slots.get(slots.size() - 1));
    }

    @Test
    void lastSlotMustEndByClosingTime() {
        // 08:00–09:30 -> 08:00, 08:30, 09:00 (khung 09:00 kết thúc đúng 09:30)
        assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0)),
                service.cutSlots(LocalTime.of(8, 0), LocalTime.of(9, 30)));
    }

    @Test
    void afternoonOnlyShiftHasNoLunchSlots() {
        assertEquals(4, service.cutSlots(LocalTime.of(14, 0), LocalTime.of(16, 0)).size());
    }

    @Test
    void shiftInsideLunchBreakHasNoSlots() {
        assertTrue(service.cutSlots(LocalTime.of(12, 0), LocalTime.of(13, 0)).isEmpty());
    }

    @Test
    void bookedSlotsStayInListButAreNotFree() {
        LichLamViec schedule = schedule(TOMORROW, "08:00", "10:00", TrangThaiLich.lam_viec);
        when(lichHenRepository.findBookedTimes(eq(10), eq(TrangThaiLichHen.da_huy)))
                .thenReturn(List.of(LocalTime.of(8, 30)));

        List<KhungGioDto> slots = service.slotsOf(schedule);

        assertEquals(List.of("08:00", "08:30", "09:00", "09:30"), times(slots));
        assertTrue(slots.get(0).isTrong());
        assertFalse(slots.get(1).isTrong());
        assertTrue(slots.get(2).isTrong());
    }

    @Test
    void todayDropsSlotsThatAlreadyStarted() {
        // Bây giờ là 09:10: khung 08:00–09:00 đã qua, khung 09:00 đã bắt đầu nên cũng bị bỏ
        LichLamViec schedule = schedule(TODAY, "08:00", "10:30", TrangThaiLich.lam_viec);

        assertEquals(List.of("09:30", "10:00"), times(service.slotsOf(schedule)));
    }

    @Test
    void slotStartingExactlyNowIsAlreadyPast() {
        Clock atTen = Clock.fixed(Instant.parse("2026-11-23T03:00:00Z"), ZONE); // 10:00 giờ Việt Nam
        KhungTrongService atTenService = new KhungTrongService(lichLamViecRepository, lichHenRepository,
                new ClinicProperties(), atTen);
        LichLamViec schedule = schedule(TODAY, "09:00", "11:00", TrangThaiLich.lam_viec);

        assertEquals(List.of("10:30"), times(atTenService.slotsOf(schedule)));
    }

    @Test
    void pastDayHasNoSlots() {
        assertTrue(service.slotsOf(schedule(TODAY.minusDays(1), "08:00", "17:00", TrangThaiLich.lam_viec)).isEmpty());
    }

    @Test
    void futureDayKeepsAllSlots() {
        assertEquals(14, service.slotsOf(schedule(TOMORROW, "08:00", "17:00", TrangThaiLich.lam_viec)).size());
    }

    @Test
    void dayOffHasNoSlots() {
        assertTrue(service.slotsOf(schedule(TOMORROW, "08:00", "17:00", TrangThaiLich.nghi)).isEmpty());
    }

    @Test
    void findByDateReturnsEmptyWhenNoDoctorWorks() {
        when(lichLamViecRepository.findWorkingOn(TOMORROW, 1)).thenReturn(List.of());

        assertTrue(service.findByDate(TOMORROW, 1).isEmpty());
    }

    @Test
    void findByDateListsDoctorsAndOmitsThoseWithoutSlots() {
        LichLamViec open = schedule(TOMORROW, "08:00", "09:00", TrangThaiLich.lam_viec);
        open.getBacSi().setHoTen("BS A");
        LichLamViec off = schedule(TOMORROW, "08:00", "09:00", TrangThaiLich.nghi);
        when(lichLamViecRepository.findWorkingOn(TOMORROW, null)).thenReturn(List.of(open, off));

        var result = service.findByDate(TOMORROW, null);

        assertEquals(1, result.size());
        assertEquals("BS A", result.get(0).getHoTen());
        assertEquals(2, result.get(0).getKhung().size());
    }
}
