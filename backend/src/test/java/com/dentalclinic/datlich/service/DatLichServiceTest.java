package com.dentalclinic.datlich.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.springframework.dao.DataIntegrityViolationException;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.benhnhan.repository.BenhNhanRepository;
import com.dentalclinic.benhnhan.repository.TienSuBenhRepository;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ClinicProperties;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;
import com.dentalclinic.danhmuc.repository.DichVuRepository;
import com.dentalclinic.datlich.dto.BenhNhanMoiRequest;
import com.dentalclinic.datlich.dto.DatLichRequest;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;
import com.dentalclinic.datlich.repository.ChiTietLichHenRepository;
import com.dentalclinic.datlich.repository.LichHenRepository;
import com.dentalclinic.lichlamviec.entity.LichLamViec;
import com.dentalclinic.lichlamviec.entity.TrangThaiLich;
import com.dentalclinic.lichlamviec.repository.LichLamViecRepository;
import com.dentalclinic.security.UserPrincipal;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.repository.TaiKhoanRepository;

// BR-21 đến BR-25: dùng KhungTrongService thật, chỉ giả các repository
class DatLichServiceTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    // "Hôm nay" là thứ Hai 2026-11-23, 09:10
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 23);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);
    private static final UserPrincipal USER = new UserPrincipal(7, "bn", "benh_nhan", true);

    private LichLamViecRepository lichLamViecRepository;
    private LichHenRepository lichHenRepository;
    private BenhNhanRepository benhNhanRepository;
    private DichVuRepository dichVuRepository;
    private DatLichService service;

    @BeforeEach
    void setUp() {
        lichLamViecRepository = mock(LichLamViecRepository.class);
        lichHenRepository = mock(LichHenRepository.class);
        benhNhanRepository = mock(BenhNhanRepository.class);
        dichVuRepository = mock(DichVuRepository.class);
        ClinicProperties clinic = new ClinicProperties();
        Clock clock = Clock.fixed(LocalDateTime.of(TODAY, LocalTime.of(9, 10)).atZone(ZONE).toInstant(), ZONE);
        KhungTrongService khungTrongService = new KhungTrongService(lichLamViecRepository, lichHenRepository, clinic,
                clock);
        service = new DatLichService(lichLamViecRepository, lichHenRepository, mock(ChiTietLichHenRepository.class),
                benhNhanRepository, mock(TienSuBenhRepository.class), dichVuRepository,
                mock(TaiKhoanRepository.class), khungTrongService, clinic, clock);

        when(lichHenRepository.findBookedTimes(anyInt(), any())).thenReturn(List.of());
        when(lichHenRepository.countActiveAt(anyInt(), any(), any(), any())).thenReturn(0L);
        when(lichHenRepository.saveAndFlush(any(LichHen.class))).thenAnswer(inv -> {
            LichHen saved = inv.getArgument(0);
            saved.setMaLichHen(100);
            return saved;
        });
        when(benhNhanRepository.findById(5)).thenReturn(Optional.of(patient(5, USER.getMaTaiKhoan())));
    }

    private static BenhNhan patient(int id, int accountId) {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(accountId);
        BenhNhan patient = new BenhNhan();
        patient.setMaBenhNhan(id);
        patient.setTaiKhoan(account);
        patient.setHoTen("Nguyễn Văn A");
        patient.setNgaySinh(LocalDate.of(1990, 5, 1));
        return patient;
    }

    // Buổi 08:00–12:00 của bác sĩ 1; trả về để test tự đổi ngày/trạng thái
    private LichLamViec schedule(LocalDate date) {
        BacSi doctor = new BacSi();
        doctor.setMaBacSi(1);
        doctor.setHoTen("BS A");
        doctor.setTrangThaiLamViec(TrangThaiLamViec.dang_lam);
        LichLamViec schedule = new LichLamViec();
        schedule.setMaLich(10);
        schedule.setBacSi(doctor);
        schedule.setNgay(date);
        schedule.setGioBatDau(LocalTime.of(8, 0));
        schedule.setGioKetThuc(LocalTime.of(12, 0));
        schedule.setTrangThai(TrangThaiLich.lam_viec);
        when(lichLamViecRepository.findById(10)).thenReturn(Optional.of(schedule));
        return schedule;
    }

    private static DatLichRequest request(String time) {
        DatLichRequest request = new DatLichRequest();
        request.setMaBenhNhan(5);
        request.setMaLich(10);
        request.setGioHen(LocalTime.parse(time));
        return request;
    }

    private static void assertCode(ResponseCode expected, Runnable action) {
        assertEquals(expected, assertThrows(AppException.class, action::run).getResponseCode());
    }

    @Test
    void booksFreeSlotAsPendingAppointment() {
        schedule(TOMORROW);

        LichHenDto result = service.book(request("09:00"), USER);

        assertEquals(100, result.getMaLichHen());
        assertEquals("da_dat", result.getTrangThai());
        assertEquals(LocalTime.of(9, 0), result.getGioHen());
    }

    @Test
    void rejectsTimeOutsideWorkingHours() {
        schedule(TOMORROW);

        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("14:00"), USER));
    }

    @Test
    void rejectsLunchBreakTime() {
        LichLamViec full = schedule(TOMORROW);
        full.setGioKetThuc(LocalTime.of(17, 0));

        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("12:00"), USER));
    }

    @Test
    void rejectsTimeNotOnSlotBoundary() {
        schedule(TOMORROW);

        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("09:15"), USER));
    }

    @Test
    void rejectsSlotThatAlreadyStartedToday() {
        schedule(TODAY);

        // Bây giờ 09:10 nên 09:00 đã qua
        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsDayOff() {
        schedule(TOMORROW).setTrangThai(TrangThaiLich.nghi);

        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsResignedDoctor() {
        schedule(TOMORROW).getBacSi().setTrangThaiLamViec(TrangThaiLamViec.nghi_viec);

        assertCode(ResponseCode.INVALID_SLOT, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsSlotAlreadyBooked() {
        schedule(TOMORROW);
        when(lichHenRepository.findBookedTimes(10, TrangThaiLichHen.da_huy)).thenReturn(List.of(LocalTime.of(9, 0)));

        assertCode(ResponseCode.SLOT_TAKEN, () -> service.book(request("09:00"), USER));
        verify(lichHenRepository, never()).saveAndFlush(any());
    }

    @Test
    void mapsUniqueConstraintViolationToSlotTaken() {
        // Hai người đặt cùng lúc: kiểm tra ở service qua được nhưng UNIQUE ở DB chặn người sau (BR-21)
        schedule(TOMORROW);
        when(lichHenRepository.saveAndFlush(any(LichHen.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertCode(ResponseCode.SLOT_TAKEN, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsPatientAlreadyBusyAtThatTime() {
        schedule(TOMORROW);
        when(lichHenRepository.countActiveAt(5, TOMORROW, LocalTime.of(9, 0), TrangThaiLichHen.da_huy)).thenReturn(1L);

        assertCode(ResponseCode.PATIENT_TIME_CONFLICT, () -> service.book(request("09:00"), USER));
        verify(lichHenRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsDayBeyondBookingWindow() {
        // Cửa sổ 14 ngày tính cả hôm nay: ngày thứ 14 là TODAY + 13, ngày thứ 15 thì không
        schedule(TODAY.plusDays(13));
        assertEquals(100, service.book(request("09:00"), USER).getMaLichHen());

        schedule(TODAY.plusDays(14));
        assertCode(ResponseCode.BOOKING_OUT_OF_RANGE, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsPastDay() {
        schedule(TODAY.minusDays(1));

        assertCode(ResponseCode.BOOKING_OUT_OF_RANGE, () -> service.book(request("09:00"), USER));
    }

    @Test
    void rejectsPatientOfAnotherAccount() {
        schedule(TOMORROW);
        when(benhNhanRepository.findById(5)).thenReturn(Optional.of(patient(5, 999)));

        assertCode(ResponseCode.PATIENT_NOT_FOUND, () -> service.book(request("09:00"), USER));
    }

    @Test
    void requiresExactlyOneOfExistingOrNewPatient() {
        schedule(TOMORROW);
        DatLichRequest neither = request("09:00");
        neither.setMaBenhNhan(null);
        DatLichRequest both = request("09:00");
        both.setBenhNhanMoi(new BenhNhanMoiRequest());

        assertCode(ResponseCode.BAD_REQUEST, () -> service.book(neither, USER));
        assertCode(ResponseCode.BAD_REQUEST, () -> service.book(both, USER));
    }

    @Test
    void rejectsDuplicateNewPatient() {
        schedule(TOMORROW);
        BenhNhanMoiRequest input = new BenhNhanMoiRequest();
        input.setHoTen(" Nguyễn Văn A ");
        input.setNgaySinh(LocalDate.of(1990, 5, 1));
        DatLichRequest request = request("09:00");
        request.setMaBenhNhan(null);
        request.setBenhNhanMoi(input);
        when(benhNhanRepository.existsByTaiKhoan_MaTaiKhoanAndHoTenAndNgaySinh(7, "Nguyễn Văn A",
                LocalDate.of(1990, 5, 1))).thenReturn(true);

        assertCode(ResponseCode.PATIENT_DUPLICATE, () -> service.book(request, USER));
        verify(benhNhanRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsInactiveOrUnknownService() {
        schedule(TOMORROW);
        DichVu stopped = new DichVu();
        stopped.setMaDichVu(3);
        stopped.setTenDichVu("Đã ngưng");
        stopped.setTrangThai(TrangThaiDichVu.ngung);
        when(dichVuRepository.findAllById(any())).thenReturn(List.of(stopped));
        DatLichRequest request = request("09:00");
        request.setDichVuDangKy(List.of(3));

        assertCode(ResponseCode.SERVICE_NOT_FOUND, () -> service.book(request, USER));
        verify(lichHenRepository, never()).saveAndFlush(any());
    }
}
