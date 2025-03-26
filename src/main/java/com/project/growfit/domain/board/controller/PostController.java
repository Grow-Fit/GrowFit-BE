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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "글 삭제", description = "글 작성자는 글을 삭제할 수 있다.")
    @DeleteMapping("/{postId}")
    public ResultResponse<String> deletePost(@PathVariable Long postId) {
        int imageCnt = postService.deletePost(postId);
        return new ResultResponse<>(ResultCode.DELETE_POST_SUCCESS, "사진 " + imageCnt + "장을 포함하여 글이 삭제되었습니다.");
    }

    @Operation(summary = "글 수정", description = "글 작성자는 글을 수정할 수 있습니다. 글 조회 후, 내용 및 이미지 수정 후 수정하는 용도로 사용합니다.")
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<String> updatePost(@RequestPart("dto") @Valid PostRequestDto dto,
                                             @RequestPart(value = "images", required = false)List<MultipartFile> images,
                                             @PathVariable Long postId
    ) throws IOException {
        Post post = postService.updatePost(dto, images, postId);
        return new ResultResponse<>(ResultCode.CREATE_POST_SUCCESS, "id: " + post.getId() + " 글이 수정되었습니다.");
    }

    @Operation(summary = "글 좋아요", description = "글에 좋아요를 할 수 있습니다. 좋아요가 비활성화된 상태에서 요청을 하면 좋아요가 활성화 되고, 활성화된 상태에서 요청을 하면 좋아요가 비활성화 됩니다.")
    @PostMapping("/{postId}/like")
    public ResultResponse<String> likePost(@PathVariable Long postId) {
        boolean isLike = postService.postLike(postId);
        if (isLike) return new ResultResponse<>(ResultCode.LIKE_POST_SUCCESS, "id: " + postId + " 번 글이 좋아요 처리되었습니다.");
        else return new ResultResponse<>(ResultCode.DISLIKE_POST_SUCCESS, "id: " + postId + " 번 글 좋아요 취소 처리되었습니다.");
    }
}
