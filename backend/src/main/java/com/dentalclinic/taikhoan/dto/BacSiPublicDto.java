package com.dentalclinic.taikhoan.dto;

import com.dentalclinic.taikhoan.entity.BacSi;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Thông tin bác sĩ cho khách xem, không có thông tin liên lạc hay tài khoản
@Getter
@AllArgsConstructor
public class BacSiPublicDto {
    private final Integer maBacSi;
    private final String hoTen;
    private final String chuyenMon;
    private final String bangCap;
    private final String gioiThieu;
    private final String anhDaiDien;

    public static BacSiPublicDto of(BacSi doctor) {
        return new BacSiPublicDto(doctor.getMaBacSi(), doctor.getHoTen(), doctor.getChuyenMon(),
                doctor.getBangCap(), doctor.getGioiThieu(), doctor.getAnhDaiDien());
    }
}
