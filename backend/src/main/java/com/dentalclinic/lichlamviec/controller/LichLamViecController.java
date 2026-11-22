package com.dentalclinic.lichlamviec.controller;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.lichlamviec.dto.DoiLichLamViecDto;
import com.dentalclinic.lichlamviec.dto.LichLamViecCapNhatRequest;
import com.dentalclinic.lichlamviec.dto.LichLamViecDto;
import com.dentalclinic.lichlamviec.dto.LichTuanRequest;
import com.dentalclinic.lichlamviec.dto.SaoChepTuanDto;
import com.dentalclinic.lichlamviec.dto.SaoChepTuanRequest;
import com.dentalclinic.lichlamviec.service.LichLamViecService;
import com.dentalclinic.security.UserPrincipal;

@RestController
@RequestMapping("/lich-lam-viec")
@RequiredArgsConstructor
public class LichLamViecController {
    private final LichLamViecService lichLamViecService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NHAN_VIEN', 'BAC_SI', 'CHU_PHONG_KHAM')")
    public ApiResponse<List<LichLamViecDto>> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer maBacSi,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(lichLamViecService.list(tuNgay, denNgay, maBacSi, user));
    }

    @PostMapping("/sao-chep-tuan")
    @PreAuthorize("hasRole('CHU_PHONG_KHAM')")
    public ApiResponse<SaoChepTuanDto> copyPreviousWeek(@Valid @RequestBody SaoChepTuanRequest request) {
        return ApiResponse.success(lichLamViecService.copyPreviousWeek(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('CHU_PHONG_KHAM')")
    public ApiResponse<DoiLichLamViecDto> update(@PathVariable Integer id,
            @Valid @RequestBody LichLamViecCapNhatRequest request) {
        return ApiResponse.success(lichLamViecService.update(id, request));
    }

    @PutMapping("/tuan")
    @PreAuthorize("hasRole('CHU_PHONG_KHAM')")
    public ApiResponse<List<LichLamViecDto>> saveWeek(@Valid @RequestBody LichTuanRequest request) {
        return ApiResponse.success(lichLamViecService.saveWeek(request));
    }
}
