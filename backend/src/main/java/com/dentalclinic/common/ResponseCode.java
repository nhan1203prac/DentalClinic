package com.dentalclinic.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// Mã trả về chung cho mọi response; frontend dựa vào name() để xử lý, message chỉ là mặc định hiển thị
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(HttpStatus.OK, "Thành công"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    INVALID_AVATAR_URL(HttpStatus.BAD_REQUEST, "Ảnh đại diện phải là link Cloudinary của phòng khám"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập hoặc phiên đã hết hạn"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn, hãy đăng nhập lại"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),
    ACCOUNT_LOCK_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Chỉ được khóa tài khoản của nhân viên và bác sĩ"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"),
    STAFF_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"),
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bác sĩ"),
    SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức không được hỗ trợ"),
    DATA_CONFLICT(HttpStatus.CONFLICT, "Dữ liệu xung đột với dữ liệu đã có"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau");

    private final HttpStatus status;
    private final String message;
}
