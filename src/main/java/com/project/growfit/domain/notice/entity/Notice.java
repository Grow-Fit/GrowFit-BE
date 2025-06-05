package com.project.growfit.domain.notice.entity;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.notice.dto.request.NoticeRequestDto;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "message", nullable = false, length = 200)
    private String message;

    @Column(name = "notice_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "weekly_goal_id")
    private Long weeklyGoalId;

    public static Notice createNoticeParentToChild(NoticeRequestDto dto, Child child, NoticeType noticeType) {
        Notice notice = new Notice();
        notice.targetType = TargetType.CHILD;
        notice.targetId = child.getId();
        notice.message = dto.getMessage();
        notice.noticeType = noticeType;
        if (noticeType.equals(NoticeType.PRAISE_LETTER)) notice.weeklyGoalId = child.getId();  // 부모가 편지를 작성한 경우(목표보드), 해당 주간 목표 id 저장

        return notice;
    }

    public static Notice createNoticeChildToParent(NoticeRequestDto dto, Parent parent, NoticeType noticeType) {
        Notice notice = new Notice();
        notice.targetType = TargetType.PARENT;
        notice.targetId = parent.getId();
        notice.message = dto.getMessage();
        notice.noticeType = noticeType;

        return notice;
    }

    public void readNotice() {
        this.isRead = true;
    }
}
