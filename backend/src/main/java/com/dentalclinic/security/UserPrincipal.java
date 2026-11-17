package com.dentalclinic.security;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.dentalclinic.taikhoan.entity.TaiKhoan;
import com.dentalclinic.taikhoan.entity.TrangThaiTaiKhoan;

// Người dùng đang đăng nhập; controller lấy maTaiKhoan từ đây để kiểm tra quyền sở hữu dữ liệu
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Integer maTaiKhoan;
    private final String tenDangNhap;
    private final String vaiTro;
    private final boolean hoatDong;

    public static UserPrincipal of(TaiKhoan account) {
        return new UserPrincipal(account.getMaTaiKhoan(), account.getTenDangNhap(), account.getVaiTro().name(),
                account.getTrangThai() == TrangThaiTaiKhoan.hoat_dong);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @Override
    public boolean isEnabled() {
        return hoatDong;
    }
}
