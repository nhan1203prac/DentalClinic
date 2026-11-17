package com.dentalclinic.datlich.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.lotrinh.entity.BuocDieuTri;

@Entity
@Table(name = "CHI_TIET_LICH_HEN")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietLichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet")
    private Integer maChiTiet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_lich_hen", nullable = false)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_dich_vu", nullable = false)
    private DichVu dichVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_buoc")
    private BuocDieuTri buoc;

    // FDI 11–48; cột TINYINT UNSIGNED nên khai báo TINYINT để validate khớp
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "so_rang")
    private Short soRang;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiChiTiet trangThai = TrangThaiChiTiet.dang_ky;

    @Column(name = "ghi_chu")
    private String ghiChu;
}
