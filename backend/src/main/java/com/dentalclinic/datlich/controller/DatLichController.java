package com.dentalclinic.datlich.controller;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.datlich.dto.BacSiKhungDto;
import com.dentalclinic.datlich.dto.BenhNhanDto;
import com.dentalclinic.datlich.dto.DatLichRequest;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.service.DatLichService;
import com.dentalclinic.datlich.service.KhungTrongService;
import com.dentalclinic.security.UserPrincipal;

// Luồng đặt lịch của bệnh nhân: xem khung trống, chọn người khám, đặt lịch
@RestController
@RequiredArgsConstructor
public class DatLichController {
    private final KhungTrongService khungTrongService;
    private final DatLichService datLichService;

    @GetMapping("/dat-lich/khung-trong")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public ApiResponse<List<BacSiKhungDto>> freeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            @RequestParam(required = false) Integer maBacSi) {
        return ApiResponse.success(khungTrongService.findByDate(ngay, maBacSi));
    }

    @GetMapping("/me/benh-nhan")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public ApiResponse<List<BenhNhanDto>> myPatients(@AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(datLichService.myPatients(user));
    }

    @PostMapping("/lich-hen")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BENH_NHAN')")
    public ApiResponse<LichHenDto> book(@Valid @RequestBody DatLichRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(datLichService.book(request, user));
    }
}
