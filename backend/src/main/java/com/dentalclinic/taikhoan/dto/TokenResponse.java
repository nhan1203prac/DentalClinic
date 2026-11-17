package com.dentalclinic.taikhoan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Refresh token không nằm trong body, được đặt vào cookie HttpOnly
@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;
    private final TaiKhoanDto taiKhoan;
}
