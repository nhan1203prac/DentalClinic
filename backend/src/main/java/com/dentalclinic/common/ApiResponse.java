package com.dentalclinic.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T result;

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T result) {
        return of(ResponseCode.SUCCESS, result);
    }

    public static <T> ApiResponse<T> of(ResponseCode responseCode, T result) {
        return new ApiResponse<>(responseCode.name(), responseCode.getMessage(), result);
    }

    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return of(responseCode, null);
    }

    public static <T> ApiResponse<T> error(ResponseCode responseCode, String message) {
        return new ApiResponse<>(responseCode.name(), message, null);
    }
}
