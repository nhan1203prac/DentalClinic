package com.dentalclinic.kham.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.taikhoan.entity.NhanVien;

@Entity
@Table(name = "HO_SO_KHAM")
@Getter
@Setter
@NoArgsConstructor
public class HoSoKham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ho_so_kham")
    private Integer maHoSoKham;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_lich_hen", nullable = false, unique = true)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @CreationTimestamp
    @Column(name = "ngay_kham", nullable = false, updatable = false)
    private LocalDateTime ngayKham;

    @Column(name = "so_thu_tu", nullable = false)
    private Integer soThuTu;

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String trieuChung;

    @Column(name = "chan_doan", columnDefinition = "TEXT")
    private String chanDoan;

    @Column(name = "noi_dung_dieu_tri", columnDefinition = "TEXT")
    private String noiDungDieuTri;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
}
