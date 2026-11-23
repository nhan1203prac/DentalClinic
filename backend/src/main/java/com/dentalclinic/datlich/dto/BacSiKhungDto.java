package com.dentalclinic.datlich.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Các khung khám của một bác sĩ trong ngày được hỏi
@Getter
@AllArgsConstructor
public class BacSiKhungDto {
    private final Integer maBacSi;
    private final String hoTen;
    private final Integer maLich;
    private final List<KhungGioDto> khung;
}
