package com.dentalclinic.datlich.repository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.datlich.entity.TrangThaiLichHen;

public interface LichHenRepository extends JpaRepository<LichHen, Integer> {

    List<LichHen> findByLichLamViec_MaLichAndTrangThaiInOrderByGioHen(Integer scheduleId,
            Collection<TrangThaiLichHen> statuses);

    long countByLichLamViec_MaLich(Integer scheduleId);

    long countByLichLamViec_MaLichAndTrangThaiNot(Integer scheduleId, TrangThaiLichHen status);

    // Giờ các lịch hẹn còn hiệu lực của một buổi làm việc (khung đã bị giữ chỗ)
    @Query("select l.gioHen from LichHen l where l.lichLamViec.maLich = :scheduleId and l.trangThai <> :cancelled")
    List<LocalTime> findBookedTimes(@Param("scheduleId") Integer scheduleId,
            @Param("cancelled") TrangThaiLichHen cancelled);

    // BR-23: số lịch hẹn còn hiệu lực của một người khám vào đúng ngày và giờ, với mọi bác sĩ
    @Query("""
            select count(l) from LichHen l
            where l.benhNhan.maBenhNhan = :patientId and l.lichLamViec.ngay = :date
              and l.gioHen = :time and l.trangThai <> :cancelled""")
    long countActiveAt(@Param("patientId") Integer patientId, @Param("date") java.time.LocalDate date,
            @Param("time") LocalTime time, @Param("cancelled") TrangThaiLichHen cancelled);

    // Mọi lịch hẹn của các người khám thuộc một tài khoản, mới nhất trước; kèm sẵn người khám và bác sĩ
    @Query("""
            select l from LichHen l join fetch l.benhNhan p join fetch l.lichLamViec s join fetch s.bacSi
            where p.taiKhoan.maTaiKhoan = :accountId
            order by s.ngay desc, l.gioHen desc""")
    List<LichHen> findByAccount(@Param("accountId") Integer accountId);

    @Query("""
            select l from LichHen l join fetch l.benhNhan p join fetch l.lichLamViec s join fetch s.bacSi
            where l.maLichHen = :id""")
    java.util.Optional<LichHen> findDetailById(@Param("id") Integer id);

    // Mỗi phần tử: [maLich, số lịch hẹn còn hiệu lực]
    @Query("""
            select l.lichLamViec.maLich, count(l) from LichHen l
            where l.lichLamViec.maLich in :scheduleIds and l.trangThai <> :cancelled
            group by l.lichLamViec.maLich""")
    List<Object[]> countActiveBySchedule(@Param("scheduleIds") Collection<Integer> scheduleIds,
            @Param("cancelled") TrangThaiLichHen cancelled);
}
