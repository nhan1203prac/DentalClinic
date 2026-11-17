package com.dentalclinic.taikhoan.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.taikhoan.dto.NhanVienCapNhatRequest;
import com.dentalclinic.taikhoan.dto.NhanVienDto;
import com.dentalclinic.taikhoan.dto.NhanVienRequest;
import com.dentalclinic.taikhoan.entity.NhanVien;
import com.dentalclinic.taikhoan.entity.VaiTro;
import com.dentalclinic.taikhoan.repository.NhanVienRepository;

@Service
@RequiredArgsConstructor
public class NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanService taiKhoanService;

    // Lấy tài khoản qua quan hệ lazy nên cần transaction
    @Transactional(readOnly = true)
    public List<NhanVienDto> list() {
        return nhanVienRepository.findAll().stream().map(NhanVienDto::of).toList();
    }

    // BR-04: tạo TAI_KHOAN và NHAN_VIEN trong một transaction
    @Transactional
    public NhanVienDto create(NhanVienRequest request) {
        NhanVien staff = new NhanVien();
        staff.setTaiKhoan(taiKhoanService.create(request, VaiTro.nhan_vien));
        staff.setHoTen(request.getHoTen().trim());
        staff.setNgayVaoLam(request.getNgayVaoLam());
        return NhanVienDto.of(nhanVienRepository.save(staff));
    }

    @Transactional
    public NhanVienDto update(Integer staffId, NhanVienCapNhatRequest request) {
        NhanVien staff = nhanVienRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ResponseCode.STAFF_NOT_FOUND));
        staff.setHoTen(request.getHoTen().trim());
        staff.setNgayVaoLam(request.getNgayVaoLam());
        staff.setTrangThaiLamViec(request.getTrangThaiLamViec());
        taiKhoanService.applyContact(staff.getTaiKhoan(), request);
        return NhanVienDto.of(staff);
    }
}
