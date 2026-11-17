package com.dentalclinic.danhmuc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;

public interface DichVuRepository extends JpaRepository<DichVu, Integer> {
    List<DichVu> findByTrangThai(TrangThaiDichVu trangThai);
}
