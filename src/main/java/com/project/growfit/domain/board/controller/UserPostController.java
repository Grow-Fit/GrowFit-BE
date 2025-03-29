package com.project.growfit.domain.board.controller;

import com.project.growfit.domain.board.dto.response.BookmarkResponseListDto;
import com.project.growfit.domain.board.service.UserPostService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "부모 커뮤니티", description = "부모 커뮤니티와 관련된 API")
public class UserPostController {

    private final UserPostService userPostService;

    @Operation(summary = "북마크 글 리스트 조회", description = "로그인 한 본인은 본인이 북마크한 글들을 조회할 수 있습니다.")
    @GetMapping("/{userId}/bookmarks")
    public ResultResponse<List<BookmarkResponseListDto>> getBookmarks(@PathVariable Long userId) {
        List<BookmarkResponseListDto> list = userPostService.getBookmarks(userId);
        return new ResultResponse<>(ResultCode.GET_BOOKMARKS_SUCCESS, list);
    }
}
