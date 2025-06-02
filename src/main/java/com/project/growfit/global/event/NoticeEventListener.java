package com.project.growfit.global.event;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.notice.entity.Notice;
import com.project.growfit.domain.notice.entity.NoticeType;
import com.project.growfit.domain.notice.entity.TargetType;
import com.project.growfit.domain.notice.service.SseEmitterService;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NoticeEventListener {

    private final SseEmitterService sseEmitterService;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNoticeSaved(NoticeSavedEvent event) {
        Notice notice = event.getNotice();
        String userId = resolveUserId(notice.getTargetType(), notice.getTargetId());
        String userRole = (notice.getTargetType() == TargetType.PARENT) ? "ROLE_PARENT" : "ROLE_CHILD";

        Map<String, Object> noticeMap = Map.of(
                "id", notice.getId(),
        "message", notice.getMessage(),
                "createdAt", getFormattedDate(notice.getCreatedAt())
        );
        sseEmitterService.send(userId, userRole, NoticeType.PRAISE_LETTER, "이번 미션도 잘했어요~", noticeMap);
    }

    public String resolveUserId(TargetType targetType, Long targetId) {
        if (targetType == TargetType.PARENT) {
            Parent parent = parentRepository.findById(targetId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            return parent.getEmail();  // userId는 Parent의 email
        } else if (targetType == TargetType.CHILD) {
            Child child = childRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
            return child.userId();  // userId는 Child의 userId
        }
        throw new BusinessException(ErrorCode.WRONG_TARGET_TYPE);
    }

    public String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
