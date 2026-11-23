package com.dentalclinic.datlich.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.datlich.dto.LichHenDto;
import com.dentalclinic.datlich.service.LichKhamService;
import com.dentalclinic.security.UserPrincipal;

// Lịch khám của bệnh nhân: xem và tự hủy
@RestController
@RequiredArgsConstructor
public class LichHenController {
    private final LichKhamService lichKhamService;

    @GetMapping("/me/lich-hen")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public ApiResponse<List<LichHenDto>> myAppointments(@AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(lichKhamService.myAppointments(user));
    }

    @PatchMapping("/lich-hen/{id}/huy")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public ApiResponse<LichHenDto> cancel(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(lichKhamService.cancel(id, user));
    }
}
