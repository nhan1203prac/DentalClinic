package com.dentalclinic.taikhoan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.taikhoan.entity.BacSi;
import com.dentalclinic.taikhoan.entity.TrangThaiLamViec;

public interface BacSiRepository extends JpaRepository<BacSi, Integer> {
    List<BacSi> findByTrangThaiLamViec(TrangThaiLamViec trangThaiLamViec);

    Optional<BacSi> findByTaiKhoan_MaTaiKhoan(Integer accountId);

    Optional<BacSi> findByMaBacSiAndTrangThaiLamViec(Integer maBacSi, TrangThaiLamViec trangThaiLamViec);
}
