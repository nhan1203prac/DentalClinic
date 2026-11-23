package com.dentalclinic.benhnhan.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.benhnhan.entity.TienSuBenh;

public interface TienSuBenhRepository extends JpaRepository<TienSuBenh, Integer> {
    List<TienSuBenh> findByBenhNhan_MaBenhNhanInOrderByMaTienSu(Collection<Integer> patientIds);
}
