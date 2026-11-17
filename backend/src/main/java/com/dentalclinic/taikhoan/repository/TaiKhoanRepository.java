package com.dentalclinic.taikhoan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.taikhoan.entity.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);
}
