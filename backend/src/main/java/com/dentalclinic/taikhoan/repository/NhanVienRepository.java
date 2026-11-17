package com.dentalclinic.taikhoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.taikhoan.entity.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
}
