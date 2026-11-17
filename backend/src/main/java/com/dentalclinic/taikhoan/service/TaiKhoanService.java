package com.dentalclinic.taikhoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.taikhoan.dto.LienHeRequest;
import com.dentalclinic.taikhoan.dto.TaiKhoanDto;
import com.dentalclinic.taikhoan.dto.TaiKhoanMoiRequest;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;
import com.dentalclinic.taikhoan.entity.VaiTro;
import com.dentalclinic.taikhoan.repository.TaiKhoanRepository;

@Service
@RequiredArgsConstructor
public class TaiKhoanService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    // BR-01, BR-04: mật khẩu băm BCrypt, tên đăng nhập không trùng
    public TaiKhoan create(TaiKhoanMoiRequest request, VaiTro role) {
        String username = request.getTenDangNhap().trim();
        if (taiKhoanRepository.existsByTenDangNhap(username)) {
            throw duplicateUsername();
        }
        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        account.setVaiTro(role);
        applyContact(account, request);
        try {
            return taiKhoanRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException e) {
            // hai yêu cầu cùng tên chạy song song, UNIQUE của CSDL chặn người sau
            throw duplicateUsername();
        }
    }

    public void applyContact(TaiKhoan account, LienHeRequest contact) {
        account.setSoDienThoai(contact.getSoDienThoai());
        account.setEmail(contact.getEmail() == null || contact.getEmail().isBlank() ? null : contact.getEmail().trim());
    }

    // BR-05: khóa chỉ áp dụng cho nhân viên và bác sĩ, không khóa chủ phòng khám hay bệnh nhân
    public TaiKhoanDto setLocked(Integer accountId, boolean locked) {
        TaiKhoan account = taiKhoanRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ResponseCode.ACCOUNT_NOT_FOUND));
        if (account.getVaiTro() != VaiTro.nhan_vien && account.getVaiTro() != VaiTro.bac_si) {
            throw new AppException(ResponseCode.ACCOUNT_LOCK_NOT_ALLOWED);
        }
        account.setTrangThai(locked ? TrangThaiTaiKhoan.khoa : TrangThaiTaiKhoan.hoat_dong);
        return TaiKhoanDto.of(taiKhoanRepository.save(account));
    }

    private AppException duplicateUsername() {
        return new AppException(ResponseCode.USERNAME_ALREADY_EXISTS);
    }
}
