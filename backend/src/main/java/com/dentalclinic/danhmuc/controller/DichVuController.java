package com.dentalclinic.danhmuc.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.danhmuc.dto.DichVuCapNhatRequest;
import com.dentalclinic.danhmuc.dto.DichVuDto;
import com.dentalclinic.danhmuc.dto.DichVuRequest;
import com.dentalclinic.danhmuc.service.DichVuService;

@RestController
@RequestMapping("/dich-vu")
@RequiredArgsConstructor
public class DichVuController {
    private final DichVuService dichVuService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NHAN_VIEN', 'BAC_SI', 'CHU_PHONG_KHAM')")
    public ApiResponse<List<DichVuDto>> list() {
        return ApiResponse.success(dichVuService.list());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('NHAN_VIEN', 'CHU_PHONG_KHAM')")
    public ApiResponse<DichVuDto> create(@Valid @RequestBody DichVuRequest request) {
        return ApiResponse.success(dichVuService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHU_PHONG_KHAM')")
    public ApiResponse<DichVuDto> update(@PathVariable Integer id, @Valid @RequestBody DichVuCapNhatRequest request) {
        return ApiResponse.success(dichVuService.update(id, request));
    }
}
