package com.dentalclinic.datlich.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;

public interface LichHenRepository extends JpaRepository<LichHen, Integer> {

    long countByLichLamViec_MaLich(Integer scheduleId);

    long countByLichLamViec_MaLichAndTrangThaiNot(Integer scheduleId, TrangThaiLichHen status);

    // Mỗi phần tử: [maLich, số lịch hẹn còn hiệu lực]
    @Query("""
            select l.lichLamViec.maLich, count(l) from LichHen l
            where l.lichLamViec.maLich in :scheduleIds and l.trangThai <> :cancelled
            group by l.lichLamViec.maLich""")
    List<Object[]> countActiveBySchedule(@Param("scheduleIds") Collection<Integer> scheduleIds,
            @Param("cancelled") TrangThaiLichHen cancelled);
}
