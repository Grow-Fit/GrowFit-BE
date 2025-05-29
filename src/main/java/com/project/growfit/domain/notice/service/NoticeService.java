package com.project.growfit.domain.notice.service;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.notice.dto.request.NoticeRequestDto;
import com.project.growfit.domain.notice.dto.response.NoticeResponseDto;
import com.project.growfit.domain.notice.entity.Notice;
import com.project.growfit.domain.notice.entity.NoticeType;
import com.project.growfit.domain.notice.entity.TargetType;
import com.project.growfit.domain.notice.repository.NoticeRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final NoticeRepository noticeRepository;

    private static final String PARENT_USER_ROLE = "ROLE_PARENT";
    private static final String CHILD_USER_ROLE = "ROLE_CHILD";

    @Transactional
    public Notice saveParentToChild(NoticeRequestDto dto, NoticeType noticeType) {
        Parent parent = parentRepository.findByEmail(getCurrentUserDetails().getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Notice notice = Notice.createNoticeParentToChild(dto, parent.getChildren().getFirst(), noticeType);  // 현재는 아이 1명만 고려
        noticeRepository.save(notice);
        return notice;
    }

    @Transactional
    public NoticeResponseDto getNotice(Long noticeId) {
        String userRole = getCurrentUserDetails().getRole();
        TargetType targetType;
        Long targetId;
        String nickname;

        if (userRole.equals(PARENT_USER_ROLE)) {
            Parent parent = parentRepository.findByEmail(getCurrentUserDetails().getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            targetType = TargetType.PARENT;
            targetId = parent.getId();
            nickname = parent.getNickname();
        } else if (userRole.equals(CHILD_USER_ROLE)) {
            Child child = childRepository.findByLoginId(getCurrentUserDetails().getUserId()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            targetType = TargetType.CHILD;
            targetId = child.getId();
            nickname = child.getNickname();
        } else {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Notice notice = noticeRepository.findByIdAndTargetTypeAndTargetId(noticeId, targetType, targetId).orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        return NoticeResponseDto.from(notice, nickname);
    }

    private CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
