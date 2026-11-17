package com.dentalclinic.trolyao.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.taikhoan.entity.TaiKhoan;

@Entity
@Table(name = "HOI_THOAI")
@Getter
@Setter
@NoArgsConstructor
public class HoiThoai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoi_thoai")
    private Integer maHoiThoai;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tai_khoan", nullable = false)
    private TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan")
    private BenhNhan benhNhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_chuc_nang", nullable = false, length = 30)
    private LoaiChucNang loaiChucNang;

    @Column(name = "tieu_de", length = 200)
    private String tieuDe;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
}
