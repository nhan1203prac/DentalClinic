package com.dentalclinic.trolyao.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "TIN_NHAN")
@Getter
@Setter
@NoArgsConstructor
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tin_nhan")
    private Integer maTinNhan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_hoi_thoai", nullable = false)
    private HoiThoai hoiThoai;

    @Enumerated(EnumType.STRING)
    @Column(name = "nguoi_gui", nullable = false, length = 20)
    private NguoiGui nguoiGui;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "thoi_gian", nullable = false, updatable = false)
    private LocalDateTime thoiGian;
}
