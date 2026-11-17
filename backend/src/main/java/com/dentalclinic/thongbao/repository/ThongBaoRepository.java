package com.dentalclinic.thongbao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dentalclinic.thongbao.entity.ThongBao;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
}
