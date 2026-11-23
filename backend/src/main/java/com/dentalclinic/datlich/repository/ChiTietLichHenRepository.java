package com.dentalclinic.datlich.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dentalclinic.datlich.entity.ChiTietLichHen;

public interface ChiTietLichHenRepository extends JpaRepository<ChiTietLichHen, Integer> {

    // Chi tiết dịch vụ của nhiều lịch hẹn, kèm dịch vụ để lấy tên
    @Query("select c from ChiTietLichHen c join fetch c.dichVu where c.lichHen.maLichHen in :ids order by c.maChiTiet")
    List<ChiTietLichHen> findWithServiceByAppointments(@Param("ids") Collection<Integer> appointmentIds);
}
