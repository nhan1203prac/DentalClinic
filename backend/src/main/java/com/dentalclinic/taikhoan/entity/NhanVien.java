package com.dentalclinic.taikhoan.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NHAN_VIEN")
@Getter
@Setter
@NoArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhan_vien")
    private Integer maNhanVien;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tai_khoan", nullable = false, unique = true)
    private TaiKhoan taiKhoan;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ngay_vao_lam")
    private LocalDate ngayVaoLam;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_lam_viec", nullable = false, length = 20)
    private TrangThaiLamViec trangThaiLamViec = TrangThaiLamViec.dang_lam;
}
