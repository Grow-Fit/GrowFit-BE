package com.project.growfit.global.response;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ErrorResponse.FieldError;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class ErrorResponseTest {

    @Test
    @DisplayName("에러 응답 생성 테스트")
    void createErrorResponseWithCodeOnly() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode);

        // then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMessage()).isEqualTo("internal server error");
        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("에러 응답 생성 테스트 - 필드 에러")
    void createErrorResponseWithFieldErrors() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        List<FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError("field1", "value1", "Invalid format"),
                new ErrorResponse.FieldError("field2", "value2", "Must not be null")
        );

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, fieldErrors);

        // then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("invalid input type");
        assertThat(response.getErrors()).hasSize(2);
        assertThat(response.getErrors().get(0).getField()).isEqualTo("field1");
        assertThat(response.getErrors().get(0).getValue()).isEqualTo("value1");
        assertThat(response.getErrors().get(0).getReason()).isEqualTo("Invalid format");
        assertThat(response.getErrors().get(1).getField()).isEqualTo("field2");
        assertThat(response.getErrors().get(1).getValue()).isEqualTo("value2");
        assertThat(response.getErrors().get(1).getReason()).isEqualTo("Must not be null");
    }

    @Test
    @DisplayName("에러 응답 생성 테스트 - MethodArgumentTypeMismatchException")
    void createErrorResponseWithMethodArgumentTypeMismatchException() {
        // given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

        // when
        ErrorResponse response = ErrorResponse.of(exception);

        // then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("invalid type value");
        assertThat(response.getErrors()).isNotEmpty();
    }
}