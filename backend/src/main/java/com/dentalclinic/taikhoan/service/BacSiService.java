package com.dentalclinic.taikhoan.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.taikhoan.dto.BacSiCapNhatRequest;
import com.dentalclinic.taikhoan.dto.BacSiDto;
import com.dentalclinic.taikhoan.dto.BacSiPublicDto;
import com.dentalclinic.taikhoan.dto.BacSiRequest;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;
import com.dentalclinic.taikhoan.entity.VaiTro;
import com.dentalclinic.taikhoan.repository.BacSiRepository;

@Service
@RequiredArgsConstructor
public class BacSiService {
    private final BacSiRepository bacSiRepository;
    private final TaiKhoanService taiKhoanService;

    // Lấy tài khoản qua quan hệ lazy nên cần transaction
    @Transactional(readOnly = true)
    public List<BacSiDto> list() {
        return bacSiRepository.findAll().stream().map(BacSiDto::of).toList();
    }

    // Khách chỉ thấy bác sĩ đang làm việc
    public List<BacSiPublicDto> listPublic() {
        return bacSiRepository.findByTrangThaiLamViec(TrangThaiLamViec.dang_lam).stream()
                .map(BacSiPublicDto::of).toList();
    }

    public BacSiPublicDto getPublic(Integer doctorId) {
        return bacSiRepository.findByMaBacSiAndTrangThaiLamViec(doctorId, TrangThaiLamViec.dang_lam)
                .map(BacSiPublicDto::of)
                .orElseThrow(() -> new AppException(ResponseCode.DOCTOR_NOT_FOUND));
    }

    // BR-04: tạo TAI_KHOAN và BAC_SI trong một transaction
    @Transactional
    public BacSiDto create(BacSiRequest request) {
        BacSi doctor = new BacSi();
        doctor.setTaiKhoan(taiKhoanService.create(request, VaiTro.bac_si));
        doctor.setHoTen(request.getHoTen().trim());
        doctor.setChuyenMon(request.getChuyenMon());
        doctor.setBangCap(request.getBangCap());
        doctor.setGioiThieu(request.getGioiThieu());
        return BacSiDto.of(bacSiRepository.save(doctor));
    }

    @Transactional
    public BacSiDto update(Integer doctorId, BacSiCapNhatRequest request) {
        BacSi doctor = bacSiRepository.findById(doctorId)
                .orElseThrow(() -> new AppException(ResponseCode.DOCTOR_NOT_FOUND));
        doctor.setHoTen(request.getHoTen().trim());
        doctor.setChuyenMon(request.getChuyenMon());
        doctor.setBangCap(request.getBangCap());
        doctor.setGioiThieu(request.getGioiThieu());
        doctor.setTrangThaiLamViec(request.getTrangThaiLamViec());
        taiKhoanService.applyContact(doctor.getTaiKhoan(), request);
        return BacSiDto.of(doctor);
    }
}
