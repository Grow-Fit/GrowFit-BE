package com.project.growfit.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "잘못된 타입 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    DATA_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 처리 중 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    FAILED_TO_SAVE_USER(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 등록에 실패했습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USER_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 등록에 실패했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    PASSWORD_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 변경에 실패했습니다."),

    CHILD_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 아이입니다."),
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "아이 정보를 찾을 수 없습니다."),

    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 OAuth 제공자입니다."),
    OAUTH_ACCESS_TOKEN_ERROR(HttpStatus.BAD_GATEWAY, "OAuth 액세스 토큰 요청 중 오류가 발생했습니다."),
    OAUTH_USER_INFO_ERROR(HttpStatus.BAD_GATEWAY, "OAuth 사용자 정보를 가져오는 중 오류가 발생했습니다."),
    OAUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "OAuth 로그인에 실패했습니다."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인해주세요."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다."),

    QR_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입코드가 발급된 아이계정입니다."),
    QR_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR코드 생성을 실패하였습니다."),

    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류 발생"),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류 발생"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    //Diet
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 시간 형식입니다."),
    STICKER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 스티커가 존재하지 않습니다."),
    DIET_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 식단을 찾을 수 없습니다."),
    DAILY_DIET_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 날짜의 식단 정보가 존재하지 않습니다."),
    DIET_SET_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 식단 세트를 찾을 수 없습니다."),
    NO_SELECTED_FOOD(HttpStatus.BAD_REQUEST, "선택된 음식이 없습니다."),
    INVALID_FOOD_SOURCE_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 음식 출처 타입입니다."),
    FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "음식 정보를 찾을 수 없습니다."),
    FOOD_LIST_EMPTY(HttpStatus.BAD_REQUEST, "식단에 추가할 음식 정보가 비어있습니다."),
    DIET_IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "식단 이미지 업로드에 실패했습니다."),
    DIET_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 식단입니다."),
    INVALID_DIET_TIME_SLOT(HttpStatus.BAD_REQUEST, "잘못된 식사 시간대입니다."),
    DUPLICATE_MEALTYPE(HttpStatus.CONFLICT, "이미 해당 끼니의 식단이 존재합니다."),
    NO_FOOD_FOR_STICKER(HttpStatus.BAD_REQUEST, "음식이 등록되어야 스티커를 남길 수 있습니다.");
    private final HttpStatus status;
    private final String message;
}
