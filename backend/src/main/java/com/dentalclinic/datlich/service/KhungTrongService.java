package com.dentalclinic.datlich.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.common.ClinicProperties;
import com.dentalclinic.datlich.dto.BacSiKhungDto;
import com.dentalclinic.datlich.dto.KhungGioDto;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import com.dentalclinic.lichlamviec.repository.LichLamViecRepository;

// BR-20: tính khung khám của một bác sĩ trong một ngày
@Service
@RequiredArgsConstructor
public class KhungTrongService {
    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;
    private final ClinicProperties clinic;
    private final Clock clock;

    // API khung trống: mọi bác sĩ có khung trong ngày (hoặc chỉ một bác sĩ nếu có doctorId)
    @Transactional(readOnly = true)
    public List<BacSiKhungDto> findByDate(LocalDate date, Integer doctorId) {
        return lichLamViecRepository.findWorkingOn(date, doctorId).stream()
                .map(schedule -> new BacSiKhungDto(schedule.getBacSi().getMaBacSi(), schedule.getBacSi().getHoTen(),
                        schedule.getMaLich(), slotsOf(schedule)))
                .filter(item -> !item.getKhung().isEmpty())
                .toList();
    }

    // Khung của một buổi làm việc, sắp theo giờ. Khung đã qua giờ bị bỏ; khung đã đặt giữ lại với trong = false.
    public List<KhungGioDto> slotsOf(LichLamViec schedule) {
        if (schedule.getTrangThai() != TrangThaiLich.lam_viec) {
            return List.of();
        }
        Set<LocalTime> booked = new HashSet<>(
                lichHenRepository.findBookedTimes(schedule.getMaLich(), TrangThaiLichHen.da_huy));
        return cutSlots(schedule.getGioBatDau(), schedule.getGioKetThuc()).stream()
                .filter(time -> !isPast(schedule.getNgay(), time))
                .map(time -> new KhungGioDto(time, !booked.contains(time)))
                .toList();
    }

    // Cắt [start, end) thành các khung slotMinutes, bỏ khung chạm giờ nghỉ trưa; khung lẻ cuối buổi bị bỏ.
    // Tính bằng số phút để không bị vòng qua nửa đêm.
    public List<LocalTime> cutSlots(LocalTime start, LocalTime end) {
        int slot = clinic.getSlotMinutes();
        int lunchStart = minutes(clinic.getLunchStart());
        int lunchEnd = minutes(clinic.getLunchEnd());
        int endMinutes = minutes(end);

        List<LocalTime> slots = new ArrayList<>();
        for (int from = minutes(start); from + slot <= endMinutes; from += slot) {
            boolean overlapsLunch = from < lunchEnd && from + slot > lunchStart;
            if (!overlapsLunch) {
                slots.add(LocalTime.of(from / 60, from % 60));
            }
        }
        return slots;
    }

    // Khung đã bắt đầu (hoặc ngày đã qua) thì không đặt được nữa
    private boolean isPast(LocalDate date, LocalTime slotStart) {
        LocalDate today = LocalDate.now(clock);
        if (date.isBefore(today)) {
            return true;
        }
        return date.isEqual(today) && !slotStart.isAfter(LocalTime.now(clock));
    }

    private static int minutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }
}
