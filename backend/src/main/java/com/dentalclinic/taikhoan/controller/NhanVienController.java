package com.dentalclinic.taikhoan.controller;

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
import com.dentalclinic.taikhoan.dto.NhanVienCapNhatRequest;
import com.dentalclinic.taikhoan.dto.NhanVienDto;
import com.dentalclinic.taikhoan.dto.NhanVienRequest;
import com.dentalclinic.taikhoan.service.NhanVienService;

@RestController
@RequestMapping("/admin/nhan-vien")
@PreAuthorize("hasRole('CHU_PHONG_KHAM')")
@RequiredArgsConstructor
public class NhanVienController {
    private final NhanVienService nhanVienService;

    @GetMapping
    public ApiResponse<List<NhanVienDto>> list() {
        return ApiResponse.success(nhanVienService.list());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<NhanVienDto> create(@Valid @RequestBody NhanVienRequest request) {
        return ApiResponse.success(nhanVienService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<NhanVienDto> update(@PathVariable Integer id,
                                           @Valid @RequestBody NhanVienCapNhatRequest request) {
        return ApiResponse.success(nhanVienService.update(id, request));
    }
}
