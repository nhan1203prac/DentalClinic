package com.dentalclinic.common;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ResponseCode responseCode;

    public AppException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    // Dùng khi cần thông báo cụ thể hơn message mặc định của mã
    public AppException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
