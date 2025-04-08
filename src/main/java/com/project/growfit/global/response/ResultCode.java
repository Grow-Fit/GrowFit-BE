package com.project.growfit.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {
    RESPONSE_TEST(HttpStatus.OK, "응답 테스트 성공"),

    PARENT_NICKNAME_SET_SUCCESS(HttpStatus.OK, "부모 닉네임이 성공적으로 설정되었습니다."),
    CHILD_REGISTRATION_SUCCESS(HttpStatus.OK,  "자녀 정보가 성공적으로 등록되었습니다."),
    CHILD_INFO_RETRIEVAL_SUCCESS(HttpStatus.OK,  "자녀 정보가 성공적으로 조회되었습니다."),
    QR_GENERATION_SUCCESS(HttpStatus.OK, "아이등록 QR코드를 성공적으로 생성하였습닌다."),

    CHILD_LOGIN_SUCCESS(HttpStatus.OK, "아이 계정 로그인 성공."),
    PARENT_LOGIN_SUCCESS(HttpStatus.OK, "부모 계정 로그인 성공."),

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
    GET_PROFILE_SUCCESS(HttpStatus.OK, "프로필 조회 성공");

    private final HttpStatus status;
    private final String message;
}
