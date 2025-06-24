package com.project.growfit.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {
    RESPONSE_TEST(HttpStatus.OK, "응답 테스트 성공"),

    INFO_REGISTRATION_SUCCESS(HttpStatus.OK, "사용자 정보가 성공적으로 등록되었습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK,  "회원가입이 완료되었습니다."),
    QR_GENERATION_SUCCESS(HttpStatus.OK, "성공적으로 QR코드를 생성하였습니다."),
    ID_AVAILABLE(HttpStatus.OK, "사용 가능한 아이디입니다."),


    LOGIN_SUCCESS(HttpStatus.OK, "성공적으로 로그인하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "성공적으로 로그아웃하였습니다."),

    INFO_SUCCESS(HttpStatus.OK,  "성공적으로 정보가 조회되었습니다."),

    //Diet
    STICKER_MARK_SUCCESS(HttpStatus.OK, "스티커가 성공적으로 등록되었습니다."),
    STICKER_UPDATE_SUCCESS(HttpStatus.OK, "스티커가 수정되었습니다."),
    STICKER_DELETE_SUCCESS(HttpStatus.OK, "스티커 삭제를 성공하였습니다."),

    DIET_RETRIEVAL_SUCCESS(HttpStatus.OK, "식단을 성공적으로 조회했습니다."),
    DIET_EDIT_SUCCESS(HttpStatus.OK, "식단 정보가 성공적으로 수정되었습니다."),
    DIET_ADD_SUCCESS(HttpStatus.OK, "식단을 성공적으로 추가하였습니다."),
    DIET_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "식단이 성공적으로 삭제되었습니다."),

    DAILY_DIET_RETRIEVAL_SUCCESS(HttpStatus.OK, "일일 식단 조회에 성공하였습니다."),
    DIET_DETAIL_RETRIEVAL_SUCCESS(HttpStatus.OK, "음식 상세 정보를 성공적으로 조회하였습니다."),

    DIET_SEARCH_SUCCESS(HttpStatus.OK, "식단 음식 검색에 성공하였습니다."),
    DIET_SEARCH_RESULT_EMPTY(HttpStatus.OK, "검색 결과가 없습니다."),

    DIET_ADD_IMAGE_SUCCESS(HttpStatus.OK, "식단 사진이 성공적으로 업로드되었습니다."),
    CHILD_PHOTO_DELETE_SUCCESS(HttpStatus.OK, "식단 사진을 삭제하였습니다."),

    DIET_SET_LIST_SUCCESS(HttpStatus.OK, "식단 세트 목록 조회에 성공하였습니다."),
    DIET_SET_SAVE_SUCCESS(HttpStatus.CREATED, "식단 세트 저장에 성공하였습니다."),
    DIET_SET_RETRIEVAL_SUCCESS(HttpStatus.OK, "식단 세트 조회에 성공했습니다."),
    DIET_SET_DELETE_SUCCESS(HttpStatus.OK, "식단 세트 삭제에 성공했습니다."),
    DIET_SET_EDIT_SUCCESS(HttpStatus.OK, "식단 세트 수정에 성공했습니다." ),

    DIET_SUBMIT_SUCCESS(HttpStatus.OK, "식단 사진과 섭취 여부가 성공적으로 제출되었습니다."),
    DIET_OVERRIDE_SUCCESS(HttpStatus.OK, "식단 불이행 정보 입력에 성공했습니다."),
    CHILD_STATE_UPLOAD_SUCCESS(HttpStatus.OK, "식단 이행 상태 업데이트를 성공했습니다."),
    DIET_FOOD_DELETE_SUCCESS(HttpStatus.OK, "식단에서 음식이 성공적으로 삭제되었습니다."),
    DIET_DATE_EMPTY(HttpStatus.OK, "해당 날짜에 기록된 식단이 없습니다."),
    CALENDAR_OVERVIEW_SUCCESS(HttpStatus.OK, "식단 캘린더 조회에 성공했습니다."),

    // Goal
    WEEKLY_GOAL_CREATE_SUCCESS(HttpStatus.CREATED, "주간 목표가 성공적으로 생성되었습니다."),
    GOAL_UPDATE_SUCCESS(HttpStatus.OK, "목표가 성공적으로 수정되었습니다."),
    WEEKLY_GOAL_FETCH_SUCCESS(HttpStatus.OK, "주간 목표 조회에 성공했습니다."),
    GOAL_CERTIFICATION_SUCCESS(HttpStatus.CREATED, "목표 인증이 성공적으로 완료되었습니다."),
    CERTIFICATION_LIST_FETCH_SUCCESS(HttpStatus.OK, "인증샷 목록 조회에 성공했습니다."),

    // Letter
    LETTER_CREATE_SUCCESS(HttpStatus.CREATED, "편지가 성공적으로 작성되었습니다."),
    LETTER_FETCH_SUCCESS(HttpStatus.OK, "편지를 성공적으로 조회했습니다."),
    LETTER_LIST_FETCH_SUCCESS(HttpStatus.OK, "모든 편지를 성공적으로 조회했습니다."),

    // Community
    CREATE_POST_SUCCESS(HttpStatus.OK, "글 등록 성공."),
    GET_POST_SUCCESS(HttpStatus.OK, "글 조회 성공"),
    DELETE_POST_SUCCESS(HttpStatus.OK, "글 삭제 성공"),
    LIKE_POST_SUCCESS(HttpStatus.OK, "좋아요 성공"),
    DISLIKE_POST_SUCCESS(HttpStatus.OK, "좋아요 취소 성공"),
    BOOKMARK_POST_SUCCESS(HttpStatus.OK, "북마크 등록 성공"),
    CANCEL_BOOKMARK_POST_SUCCESS(HttpStatus.OK, "북마크 등록 취소 성공"),
    GET_BOOKMARKS_SUCCESS(HttpStatus.OK, "북마크한 글 리스트 조회 성공"),
    GET_MY_POST_SUCCESS(HttpStatus.OK, "나의 작성 글 리스트 조회 성공"),
    COMMENT_POST_SUCCESS(HttpStatus.OK, "댓글 작성 성공"),
    GET_COMMENTS_SUCCESS(HttpStatus.OK, "댓글 조회 성공"),
    GET_PROFILE_SUCCESS(HttpStatus.OK, "프로필 조회 성공"),
    UPDATE_PROFILE_SUCCESS(HttpStatus.OK, "프로필 수정 성공"),

    GET_NOTICE_SUCCESS(HttpStatus.OK, "알림 조회 성공");

    private final HttpStatus status;
    private final String message;
}
