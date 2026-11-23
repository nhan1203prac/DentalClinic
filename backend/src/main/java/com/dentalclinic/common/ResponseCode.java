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
    INVALID_SCHEDULE(HttpStatus.BAD_REQUEST, "Lịch làm việc không hợp lệ"),
    INVALID_SLOT(HttpStatus.BAD_REQUEST, "Giờ hẹn không nằm trong khung khám còn đặt được"),
    BOOKING_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Ngày hẹn nằm ngoài khoảng được phép đặt lịch"),
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
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy buổi làm việc"),
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ người khám"),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"),
    SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức không được hỗ trợ"),
    DATA_CONFLICT(HttpStatus.CONFLICT, "Dữ liệu xung đột với dữ liệu đã có"),
    SCHEDULE_HAS_APPOINTMENTS(HttpStatus.CONFLICT, "Ô lịch đã có lịch hẹn, hãy chuyển sang nghỉ để xử lý lịch hẹn"),
    APPOINTMENT_NOT_CANCELLABLE(HttpStatus.CONFLICT, "Chỉ hủy được lịch đang chờ khám và trước giờ hẹn"),
    SLOT_TAKEN(HttpStatus.CONFLICT, "Khung giờ vừa có người đặt"),
    PATIENT_TIME_CONFLICT(HttpStatus.CONFLICT, "Người khám đã có lịch hẹn khác vào cùng ngày và giờ này"),
    PATIENT_DUPLICATE(HttpStatus.CONFLICT, "Hồ sơ người khám này đã tồn tại, hãy chọn lại trong danh sách"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau");

    private final HttpStatus status;
    private final String message;
}
