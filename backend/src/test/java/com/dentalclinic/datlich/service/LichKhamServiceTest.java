package com.dentalclinic.datlich.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.ChiTietLichHenRepository;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.security.UserPrincipal;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TaiKhoan;

// BR-26: bệnh nhân chỉ hủy lịch của mình, đang da_dat, trước giờ hẹn
class LichKhamServiceTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final UserPrincipal USER = new UserPrincipal(7, "bn", "benh_nhan", true);

    private LichHenRepository lichHenRepository;
    private ChiTietLichHenRepository chiTietLichHenRepository;
    private LichKhamService service;

    @BeforeEach
    void setUp() {
        lichHenRepository = mock(LichHenRepository.class);
        chiTietLichHenRepository = mock(ChiTietLichHenRepository.class);
        // Bây giờ là 09:10 ngày 23/11
        Clock clock = Clock.fixed(LocalDateTime.of(TODAY, LocalTime.of(9, 10)).atZone(ZONE).toInstant(), ZONE);
        service = new LichKhamService(lichHenRepository, chiTietLichHenRepository, clock);
        when(chiTietLichHenRepository.findWithServiceByAppointments(List.of(1))).thenReturn(List.of());
    }

    private LichHen appointment(LocalDate date, String time, TrangThaiLichHen status, int accountId) {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(accountId);
        BenhNhan patient = new BenhNhan();
        patient.setMaBenhNhan(5);
        patient.setHoTen("Nguyễn Văn A");
        patient.setTaiKhoan(account);
        BacSi doctor = new BacSi();
        doctor.setMaBacSi(1);
        doctor.setHoTen("BS A");
        LichLamViec schedule = new LichLamViec();
        schedule.setBacSi(doctor);
        schedule.setNgay(date);
        LichHen appointment = new LichHen();
        appointment.setMaLichHen(1);
        appointment.setBenhNhan(patient);
        appointment.setLichLamViec(schedule);
        appointment.setGioHen(LocalTime.parse(time));
        appointment.setTrangThai(status);
        when(lichHenRepository.findDetailById(1)).thenReturn(Optional.of(appointment));
        return appointment;
    }

    private void assertNotCancellable(LichHen appointment) {
        TrangThaiLichHen before = appointment.getTrangThai();
        AppException e = assertThrows(AppException.class, () -> service.cancel(1, USER));
        assertEquals(ResponseCode.APPOINTMENT_NOT_CANCELLABLE, e.getResponseCode());
        assertEquals(before, appointment.getTrangThai());
    }

    @Test
    void cancelsFutureBookedAppointmentAndKeepsTheRow() {
        LichHen appointment = appointment(TODAY.plusDays(1), "09:00", TrangThaiLichHen.da_dat, 7);

        LichHenDto result = service.cancel(1, USER);

        assertEquals(TrangThaiLichHen.da_huy, appointment.getTrangThai());
        assertEquals("da_huy", result.getTrangThai());
        assertFalse(result.isCoTheHuy());
    }

    @Test
    void cannotCancelAppointmentOfAnotherAccount() {
        LichHen appointment = appointment(TODAY.plusDays(1), "09:00", TrangThaiLichHen.da_dat, 999);

        AppException e = assertThrows(AppException.class, () -> service.cancel(1, USER));

        assertEquals(ResponseCode.APPOINTMENT_NOT_FOUND, e.getResponseCode());
        assertEquals(TrangThaiLichHen.da_dat, appointment.getTrangThai());
    }

    @Test
    void cannotCancelUnknownAppointment() {
        when(lichHenRepository.findDetailById(1)).thenReturn(Optional.empty());

        assertEquals(ResponseCode.APPOINTMENT_NOT_FOUND,
                assertThrows(AppException.class, () -> service.cancel(1, USER)).getResponseCode());
    }

    @Test
    void cannotCancelAlreadyCancelledAppointment() {
        assertNotCancellable(appointment(TODAY.plusDays(1), "09:00", TrangThaiLichHen.da_huy, 7));
    }

    @Test
    void cannotCancelOnceThePatientHasArrivedOrBeenSeen() {
        for (TrangThaiLichHen status : List.of(TrangThaiLichHen.da_den, TrangThaiLichHen.dang_kham,
                TrangThaiLichHen.hoan_thanh, TrangThaiLichHen.vang_mat)) {
            assertNotCancellable(appointment(TODAY.plusDays(1), "09:00", status, 7));
        }
    }

    @Test
    void cannotCancelAfterAppointmentTime() {
        assertNotCancellable(appointment(TODAY, "09:00", TrangThaiLichHen.da_dat, 7));
        assertNotCancellable(appointment(TODAY.minusDays(1), "15:00", TrangThaiLichHen.da_dat, 7));
    }

    @Test
    void cannotCancelAtExactlyTheAppointmentTime() {
        assertNotCancellable(appointment(TODAY, "09:10", TrangThaiLichHen.da_dat, 7));
    }

    @Test
    void canCancelLaterTodayAppointment() {
        assertTrue(service.canCancel(appointment(TODAY, "09:30", TrangThaiLichHen.da_dat, 7)));
    }
}
