package com.dentalclinic.taikhoan.controller;

import com.dentalclinic.common.ApiResponse;
import com.dentalclinic.security.JwtService;
import com.dentalclinic.security.UserPrincipal;
import com.dentalclinic.taikhoan.dto.AuthResult;
import com.dentalclinic.taikhoan.dto.DangKyRequest;
import com.dentalclinic.taikhoan.dto.DangNhapRequest;
import com.dentalclinic.taikhoan.dto.TaiKhoanDto;
import com.dentalclinic.taikhoan.dto.TokenResponse;
import com.dentalclinic.taikhoan.service.AuthService;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String REFRESH_COOKIE = "refresh_token";
    // Cookie chỉ được gửi kèm các request tới /api/auth/*
    private static final String COOKIE_PATH = "/api/auth";

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${app.jwt.cookie-secure}")
    private boolean cookieSecure;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaiKhoanDto> register(@Valid @RequestBody DangKyRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody DangNhapRequest request) {
        return withRefreshCookie(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        return withRefreshCookie(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString())
                .body(ApiResponse.success());
    }

    @GetMapping("/me")
    public ApiResponse<TaiKhoanDto> me(@AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(authService.getProfile(user.getMaTaiKhoan()));
    }

    private ResponseEntity<ApiResponse<TokenResponse>> withRefreshCookie(AuthResult result) {
        ResponseCookie cookie = cookie(result.getRefreshToken(), Duration.ofSeconds(jwtService.refreshTtlSeconds()));
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(result.getBody()));
    }

    // SameSite=Strict chặn CSRF cho /auth/refresh; localhost khác cổng vẫn cùng site nên dev chạy được
    private ResponseCookie cookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true).secure(cookieSecure).sameSite("Strict")
                .path(COOKIE_PATH).maxAge(maxAge).build();
    }
}
