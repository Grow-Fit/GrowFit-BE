package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.request.CommentRequestDto;
import com.project.growfit.domain.board.dto.response.CommentResponseListDto;
import com.project.growfit.domain.board.entity.Comment;
import com.project.growfit.domain.board.repository.CommentRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ParentRepository parentRepository;

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }

    @Transactional(readOnly = true)
    public List<CommentResponseListDto> getComments(Long postId) {
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        return comments.stream().map(CommentResponseListDto::from).collect(Collectors.toList());
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        checkPostOwnership(comment.getParent());
        comment.updateComment(dto);
        return comment;
    }

    private void checkPostOwnership(Parent writer) {
        Parent loginParent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (!writer.getEmail().equals(loginParent.getEmail())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
