package com.dentalclinic.datlich.dto;

import java.time.LocalTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// BR-24: đặt lịch cho người khám cũ (maBenhNhan) hoặc mới (benhNhanMoi), đúng một trong hai
@Getter
@Setter
@NoArgsConstructor
public class DatLichRequest {
    private Integer maBenhNhan;

    @Valid
    private BenhNhanMoiRequest benhNhanMoi;

    // Tiền sử khai thêm lần này, chỉ thêm mới, không sửa hay xóa tiền sử cũ
    @Valid
    @Size(max = 30, message = "Tối đa 30 mục tiền sử")
    private List<TienSuRequest> tienSu;

    @NotNull(message = "Chưa chọn buổi làm việc")
    private Integer maLich;

    @NotNull(message = "Chưa chọn giờ hẹn")
    private LocalTime gioHen;

    @Size(max = 500, message = "Lý do khám tối đa 500 ký tự")
    private String lyDoKham;

    // Không bắt buộc chọn dịch vụ
    @Size(max = 20, message = "Tối đa 20 dịch vụ")
    private List<Integer> dichVuDangKy;
}
