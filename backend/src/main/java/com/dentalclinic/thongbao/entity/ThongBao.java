package com.dentalclinic.thongbao.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.dentalclinic.datlich.entity.LichHen;
import com.dentalclinic.lotrinh.entity.BuocDieuTri;

// BR-50: đúng một trong hai (lichHen hoặc buoc) theo loại thông báo
@Entity
@Table(name = "THONG_BAO")
@Getter
@Setter
@NoArgsConstructor
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_bao")
    private Integer maThongBao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lich_hen")
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_buoc")
    private BuocDieuTri buoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_thong_bao", nullable = false, length = 30)
    private LoaiThongBao loaiThongBao;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @CreationTimestamp
    @Column(name = "thoi_gian_gui", nullable = false, updatable = false)
    private LocalDateTime thoiGianGui;
}
