package com.dentalclinic.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.dentalclinic.taikhoan.entity.TaiKhoan;

@Service
public class JwtService {
    private static final String TOKEN_TYPE_CLAIM = "typ";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final String secret;
    private final Duration accessTtl;
    private final Duration refreshTtl;
    private SecretKey key;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-minutes}") long accessMinutes,
                      @Value("${app.jwt.refresh-days}") long refreshDays) {
        this.secret = secret;
        this.accessTtl = Duration.ofMinutes(accessMinutes);
        this.refreshTtl = Duration.ofDays(refreshDays);
    }

    @PostConstruct
    void init() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret phải dài tối thiểu 32 ký tự (đặt trong application-local.properties hoặc biến JWT_SECRET)");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(TaiKhoan account) {
        return create(account, ACCESS, accessTtl);
    }

    public String createRefreshToken(TaiKhoan account) {
        return create(account, REFRESH, refreshTtl);
    }

    public long accessTtlSeconds() {
        return accessTtl.toSeconds();
    }

    public long refreshTtlSeconds() {
        return refreshTtl.toSeconds();
    }

    // Trả mã tài khoản nếu token đúng loại và còn hạn, ngược lại null
    public Integer accountIdFromAccess(String token) {
        return accountId(token, ACCESS);
    }

    public Integer accountIdFromRefresh(String token) {
        return accountId(token, REFRESH);
    }

    private String create(TaiKhoan account, String tokenType, Duration ttl) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(account.getMaTaiKhoan()))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .claim("vaiTro", account.getVaiTro().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl.toMillis()))
                .signWith(key)
                .compact();
    }

    private Integer accountId(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            if (!expectedType.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
                return null;
            }
            return Integer.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
