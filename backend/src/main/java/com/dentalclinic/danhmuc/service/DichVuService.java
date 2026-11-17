package com.dentalclinic.danhmuc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.dentalclinic.common.AppException;
import com.dentalclinic.common.ResponseCode;
import com.dentalclinic.danhmuc.dto.DichVuCapNhatRequest;
import com.dentalclinic.danhmuc.dto.DichVuDto;
import com.dentalclinic.danhmuc.dto.DichVuRequest;
import com.dentalclinic.danhmuc.entity.DichVu;
import com.dentalclinic.danhmuc.entity.TrangThaiDichVu;
import com.dentalclinic.danhmuc.repository.DichVuRepository;

@Service
@RequiredArgsConstructor
public class DichVuService {
    private final DichVuRepository dichVuRepository;

    public List<DichVuDto> list() {
        return dichVuRepository.findAll().stream().map(DichVuDto::of).toList();
    }

    // Khách chỉ thấy dịch vụ đang áp dụng
    public List<DichVuDto> listPublic() {
        return dichVuRepository.findByTrangThai(TrangThaiDichVu.kich_hoat).stream().map(DichVuDto::of).toList();
    }

    public DichVuDto create(DichVuRequest request) {
        DichVu service = new DichVu();
        apply(service, request);
        return DichVuDto.of(dichVuRepository.save(service));
    }

    public DichVuDto update(Integer serviceId, DichVuCapNhatRequest request) {
        DichVu service = dichVuRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ResponseCode.SERVICE_NOT_FOUND));
        apply(service, request);
        service.setTrangThai(request.getTrangThai());
        return DichVuDto.of(dichVuRepository.save(service));
    }

    private void apply(DichVu service, DichVuRequest request) {
        service.setTenDichVu(request.getTenDichVu().trim());
        service.setMoTa(request.getMoTa());
        service.setGia(request.getGia());
        service.setThoiLuong(request.getThoiLuong());
    }
}
