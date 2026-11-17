package com.dentalclinic.danhmuc.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.danhmuc.dto.DichVuDto;
import com.dentalclinic.danhmuc.service.DichVuService;
import com.dentalclinic.taikhoan.dto.BacSiPublicDto;
import com.dentalclinic.taikhoan.service.BacSiService;

// Khách chưa đăng nhập xem bác sĩ và bảng giá (SecurityConfig cho phép /public/**)
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final BacSiService bacSiService;
    private final DichVuService dichVuService;

    @GetMapping("/bac-si")
    public ApiResponse<List<BacSiPublicDto>> doctors() {
        return ApiResponse.success(bacSiService.listPublic());
    }

    @GetMapping("/bac-si/{id}")
    public ApiResponse<BacSiPublicDto> doctor(@PathVariable Integer id) {
        return ApiResponse.success(bacSiService.getPublic(id));
    }

    @GetMapping("/dich-vu")
    public ApiResponse<List<DichVuDto>> services() {
        return ApiResponse.success(dichVuService.listPublic());
    }
}
