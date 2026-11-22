package com.dentalclinic.lichlamviec.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dentalclinic.lichlamviec.entity.LichLamViec;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {

    @Query("""
            select l from LichLamViec l
            where l.ngay between :from and :to and (:doctorId is null or l.bacSi.maBacSi = :doctorId)
            order by l.ngay, l.bacSi.maBacSi""")
    List<LichLamViec> findInRange(@Param("from") LocalDate from, @Param("to") LocalDate to,
            @Param("doctorId") Integer doctorId);

    Optional<LichLamViec> findByBacSi_MaBacSiAndNgay(Integer doctorId, LocalDate date);
}
