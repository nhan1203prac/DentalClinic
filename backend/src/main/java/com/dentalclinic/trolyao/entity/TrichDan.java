package com.dentalclinic.trolyao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRICH_DAN")
@Getter
@Setter
@NoArgsConstructor
public class TrichDan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_trich_dan")
    private Integer maTrichDan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tin_nhan", nullable = false)
    private TinNhan tinNhan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_tai_lieu", nullable = false)
    private TaiLieu taiLieu;

    @Column(name = "vi_tri", length = 100)
    private String viTri;
}
