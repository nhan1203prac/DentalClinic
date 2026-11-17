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
import com.dentalclinic.taikhoan.dto.BacSiCapNhatRequest;
import com.dentalclinic.taikhoan.dto.BacSiDto;
import com.dentalclinic.taikhoan.dto.BacSiRequest;
import com.dentalclinic.taikhoan.service.BacSiService;

@RestController
@RequestMapping("/admin/bac-si")
@PreAuthorize("hasRole('CHU_PHONG_KHAM')")
@RequiredArgsConstructor
public class BacSiController {
    private final BacSiService bacSiService;

    @GetMapping
    public ApiResponse<List<BacSiDto>> list() {
        return ApiResponse.success(bacSiService.list());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BacSiDto> create(@Valid @RequestBody BacSiRequest request) {
        return ApiResponse.success(bacSiService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BacSiDto> update(@PathVariable Integer id, @Valid @RequestBody BacSiCapNhatRequest request) {
        return ApiResponse.success(bacSiService.update(id, request));
    }
}
