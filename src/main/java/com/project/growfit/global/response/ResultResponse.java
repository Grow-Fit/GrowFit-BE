package com.project.growfit.global.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultResponse<T> {

    private int status;
    private String message;
    private T data;

    public ResultResponse(ResultCode resultCode, T data) {
        this.status = resultCode.getStatus().value();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static <T> ResultResponse<T> of(ResultCode resultCode, T data) {
        return new ResultResponse<>(resultCode, data);
    }

    public static <T> ResultResponse<List<T>> of(ResultCode resultCode, List<T> data) {
        return new ResultResponse<>(resultCode, data);
    }
}
