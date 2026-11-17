package com.dentalclinic.taikhoan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BAC_SI")
@Getter
@Setter
@NoArgsConstructor
public class BacSi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_bac_si")
    private Integer maBacSi;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tai_khoan", nullable = false, unique = true)
    private TaiKhoan taiKhoan;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "chuyen_mon", length = 100)
    private String chuyenMon;

    @Column(name = "bang_cap")
    private String bangCap;

    @Column(name = "gioi_thieu", columnDefinition = "TEXT")
    private String gioiThieu;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_lam_viec", nullable = false, length = 20)
    private TrangThaiLamViec trangThaiLamViec = TrangThaiLamViec.dang_lam;
}
