package com.project.growfit.domain.notice.entity;

public enum NoticeType {
    LETTER,
    GOAL_COMPLETED,  // 아이가 주간 목표를 모두 달성 완료한 경우: 아이 -> 부모
    PRAISE_LETTER,  // 부모가 아이에게 편지를 작성 완료한 경우: 부모 -> 아이
    MEAL,
    GOAL
}
