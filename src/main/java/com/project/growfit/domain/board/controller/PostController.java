package com.project.growfit.domain.board.controller;

import com.project.growfit.domain.board.dto.request.PostRequestDto;
import com.project.growfit.domain.board.dto.response.PostResponseDto;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.service.PostService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Tag(name = "부모 커뮤니티", description = "부모 커뮤니티와 관련된 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "커뮤니티 글 등록", description = "부모는 게시판, 연령대, 제목, 내용, 사진(0~4)을 입력하여 글을 등록합니다. MultipartFile 형식으로 요청을 해야 합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<String> savePost(@RequestPart("dto") @Valid PostRequestDto dto,
                                           @RequestPart(value = "images", required = false)List<MultipartFile> images
    ) throws IOException {
        Post post = postService.savePost(dto, images);
        return new ResultResponse<>(ResultCode.CREATE_POST_SUCCESS, "\"" + post.getTitle() + "\" 글이 등록되었습니다.");
    }

    @Operation(summary = "커뮤니티 글 조회", description = "부모는 게시판에서 글을 조회할 수 있습니다.")
    @GetMapping("/{postId}")
    public ResultResponse<PostResponseDto> getPost(@PathVariable Long postId) {
        PostResponseDto dto = postService.getPost(postId);
        return new ResultResponse<>(ResultCode.GET_POST_SUCCESS, dto);
    }
}
