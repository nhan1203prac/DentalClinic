package com.dentalclinic.taikhoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.security.JwtService;
import com.dentalclinic.taikhoan.dto.DangKyRequest;
import com.dentalclinic.taikhoan.dto.DangNhapRequest;
import com.dentalclinic.taikhoan.dto.TaiKhoanDto;
import com.dentalclinic.taikhoan.dto.AuthResult;
import com.dentalclinic.taikhoan.dto.TokenResponse;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;
import com.dentalclinic.taikhoan.entity.VaiTro;
import com.dentalclinic.taikhoan.repository.TaiKhoanRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TaiKhoanService taiKhoanService;

    // BR-01: đăng ký chỉ tạo TAI_KHOAN vai trò benh_nhan, mật khẩu băm BCrypt
    public TaiKhoanDto register(DangKyRequest request) {
        return TaiKhoanDto.of(taiKhoanService.create(request, VaiTro.benh_nhan));
    }

    public AuthResult login(DangNhapRequest request) {
        TaiKhoan account = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap().trim())
                .filter(found -> passwordEncoder.matches(request.getMatKhau(), found.getMatKhau()))
                .orElseThrow(() -> new AppException(ResponseCode.INVALID_CREDENTIALS));
        checkLocked(account);
        return buildToken(account);
    }

    public AuthResult refresh(String refreshToken) {
        Integer accountId = jwtService.accountIdFromRefresh(refreshToken);
        TaiKhoan account = accountId == null ? null : taiKhoanRepository.findById(accountId).orElse(null);
        if (account == null) {
            throw new AppException(ResponseCode.INVALID_REFRESH_TOKEN);
        }
        checkLocked(account);
        return buildToken(account);
    }

    public TaiKhoanDto getProfile(Integer accountId) {
        return taiKhoanRepository.findById(accountId).map(TaiKhoanDto::of)
                .orElseThrow(() -> new AppException(ResponseCode.ACCOUNT_NOT_FOUND));
    }

    // BR-05: tài khoản bị khóa không đăng nhập được
    private void checkLocked(TaiKhoan account) {
        if (account.getTrangThai() == TrangThaiTaiKhoan.khoa) {
            throw new AppException(ResponseCode.ACCOUNT_LOCKED);
        }
    }

    private AuthResult buildToken(TaiKhoan account) {
        TokenResponse body = new TokenResponse(jwtService.createAccessToken(account), "Bearer",
                jwtService.accessTtlSeconds(), TaiKhoanDto.of(account));
        return new AuthResult(body, jwtService.createRefreshToken(account));
    }
}
