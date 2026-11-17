package com.dentalclinic.lotrinh.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.dentalclinic.danhmuc.entity.DichVu;

@Entity
@Table(name = "BUOC_DIEU_TRI")
@Getter
@Setter
@NoArgsConstructor
public class BuocDieuTri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_buoc")
    private Integer maBuoc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_lo_trinh", nullable = false)
    private LoTrinhDieuTri loTrinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dich_vu")
    private DichVu dichVu;

    @Column(name = "thu_tu", nullable = false)
    private Integer thuTu;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ngay_du_kien")
    private LocalDate ngayDuKien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiBuoc trangThai = TrangThaiBuoc.chua_thuc_hien;
}
