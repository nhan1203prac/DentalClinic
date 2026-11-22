package com.dentalclinic.lichlamviec.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Lưu lưới tuần: luu = thêm hoặc sửa ô, xoa = xóa ô. Chỉ gửi các ô thay đổi.
@Getter
@Setter
@NoArgsConstructor
public class LichTuanRequest {

    @NotNull(message = "Chưa chọn tuần")
    private LocalDate tuNgay;

    @Valid
    @Size(max = 200, message = "Tối đa 200 ô mỗi lần lưu")
    private List<LichLamViecRequest> luu = new ArrayList<>();

    @Valid
    @Size(max = 200, message = "Tối đa 200 ô mỗi lần lưu")
    private List<LichLamViecKhoaRequest> xoa = new ArrayList<>();
}
