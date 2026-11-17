package com.dentalclinic.lotrinh.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.taikhoan.entity.BacSi;

@Entity
@Table(name = "LO_TRINH_DIEU_TRI")
@Getter
@Setter
@NoArgsConstructor
public class LoTrinhDieuTri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lo_trinh")
    private Integer maLoTrinh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private BacSi bacSi;

    @Column(name = "ten_lo_trinh", nullable = false, length = 150)
    private String tenLoTrinh;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiLoTrinh trangThai = TrangThaiLoTrinh.dang_thuc_hien;
}
