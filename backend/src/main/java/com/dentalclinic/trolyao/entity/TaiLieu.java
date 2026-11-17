package com.dentalclinic.trolyao.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import com.dentalclinic.taikhoan.entity.TaiKhoan;

@Entity
@Table(name = "TAI_LIEU")
@Getter
@Setter
@NoArgsConstructor
public class TaiLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tai_lieu")
    private Integer maTaiLieu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tai_khoan", nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "ten_tai_lieu", nullable = false, length = 200)
    private String tenTaiLieu;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tai_lieu", nullable = false, length = 30)
    private LoaiTaiLieu loaiTaiLieu;

    @Column(name = "duong_dan_file", nullable = false)
    private String duongDanFile;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;
}
