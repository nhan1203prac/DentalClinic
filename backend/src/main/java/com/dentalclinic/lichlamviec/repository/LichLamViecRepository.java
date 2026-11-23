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

    // Buổi làm việc của các bác sĩ đang làm trong một ngày, kèm bác sĩ để khỏi truy vấn lẻ
    @Query("""
            select l from LichLamViec l join fetch l.bacSi b
            where l.ngay = :date and b.trangThaiLamViec = com.dentalclinic.taikhoan.entity.TrangThaiLamViec.dang_lam
              and (:doctorId is null or b.maBacSi = :doctorId)
            order by b.hoTen""")
    List<LichLamViec> findWorkingOn(@Param("date") LocalDate date, @Param("doctorId") Integer doctorId);
}
