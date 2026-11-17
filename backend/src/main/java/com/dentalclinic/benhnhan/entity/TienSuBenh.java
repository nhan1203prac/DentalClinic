package com.dentalclinic.benhnhan.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "TIEN_SU_BENH")
@Getter
@Setter
@NoArgsConstructor
public class TienSuBenh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tien_su")
    private Integer maTienSu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tien_su", nullable = false, length = 20)
    private LoaiTienSu loaiTienSu;

    @Column(name = "ten_tien_su", nullable = false, length = 150)
    private String tenTienSu;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "ngay_ghi_nhan", nullable = false, updatable = false)
    private LocalDateTime ngayGhiNhan;
}
