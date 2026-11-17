package com.dentalclinic.kham.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "SO_DO_RANG")
@Getter
@Setter
@NoArgsConstructor
public class SoDoRang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_so_do_rang")
    private Integer maSoDoRang;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_ho_so_kham", nullable = false)
    private HoSoKham hoSoKham;

    // FDI 11–48; cột TINYINT UNSIGNED nên khai báo TINYINT để validate khớp
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "so_rang", nullable = false)
    private Short soRang;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang", nullable = false, length = 30)
    private TinhTrangRang tinhTrang;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
}
