package com.dentalclinic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dentalclinic.taikhoan.repository.TaiKhoanRepository;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final TaiKhoanRepository taiKhoanRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            Integer accountId = jwtService.accountIdFromAccess(header.substring(BEARER.length()));
            if (accountId != null) {
                // BR-05: đọc lại từ CSDL để tài khoản vừa bị khóa mất hiệu lực ngay, không đợi token hết hạn
                taiKhoanRepository.findById(accountId).map(UserPrincipal::of).filter(UserPrincipal::isEnabled)
                        .ifPresent(principal -> SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())));
            }
        }
        chain.doFilter(request, response);
    }
}
