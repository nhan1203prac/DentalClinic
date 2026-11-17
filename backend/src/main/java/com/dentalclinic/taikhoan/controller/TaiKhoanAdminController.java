package com.dentalclinic.taikhoan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.taikhoan.dto.KhoaTaiKhoanRequest;
import com.dentalclinic.taikhoan.dto.TaiKhoanDto;
import com.dentalclinic.taikhoan.service.TaiKhoanService;

@RestController
@RequestMapping("/admin/tai-khoan")
@PreAuthorize("hasRole('CHU_PHONG_KHAM')")
@RequiredArgsConstructor
public class TaiKhoanAdminController {
    private final TaiKhoanService taiKhoanService;

    @PatchMapping("/{id}/khoa")
    public ApiResponse<TaiKhoanDto> lock(@PathVariable Integer id, @Valid @RequestBody KhoaTaiKhoanRequest request) {
        return ApiResponse.success(taiKhoanService.setLocked(id, request.getKhoa()));
    }
}
