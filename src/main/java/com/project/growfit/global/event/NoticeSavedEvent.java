package com.project.growfit.global.event;

import com.project.growfit.domain.notice.entity.Notice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NoticeSavedEvent {
    private final Notice notice;
}
