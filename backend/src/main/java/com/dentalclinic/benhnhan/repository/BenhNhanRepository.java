package com.dentalclinic.benhnhan.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.benhnhan.entity.BenhNhan;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, Integer> {
    List<BenhNhan> findByTaiKhoan_MaTaiKhoanOrderByMaBenhNhan(Integer accountId);

    // BR-03: không trùng bộ (tài khoản, họ tên, ngày sinh)
    boolean existsByTaiKhoan_MaTaiKhoanAndHoTenAndNgaySinh(Integer accountId, String hoTen, LocalDate ngaySinh);
}
