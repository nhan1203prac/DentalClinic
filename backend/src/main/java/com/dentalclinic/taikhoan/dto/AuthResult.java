package com.dentalclinic.taikhoan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {
    private final TokenResponse body;
    private final String refreshToken;
}
