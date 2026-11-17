package com.dentalclinic.datlich.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.dentalclinic.benhnhan.entity.BenhNhan;
import com.dentalclinic.lichlamviec.entity.LichLamViec;

// con_hieu_luc là cột sinh của MySQL (BR-21), không map vào entity
@Entity
@Table(name = "LICH_HEN")
@Getter
@Setter
@NoArgsConstructor
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lich_hen")
    private Integer maLichHen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_lich", nullable = false)
    private LichLamViec lichLamViec;

    @Column(name = "gio_hen", nullable = false)
    private LocalTime gioHen;

    @Column(name = "ly_do_kham", length = 500)
    private String lyDoKham;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_lich_hen", nullable = false, length = 20)
    private LoaiLichHen loaiLichHen = LoaiLichHen.kham_moi;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiLichHen trangThai = TrangThaiLichHen.da_dat;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
}
