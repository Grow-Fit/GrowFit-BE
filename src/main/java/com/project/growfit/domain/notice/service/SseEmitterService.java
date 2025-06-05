package com.project.growfit.domain.notice.service;

import com.project.growfit.domain.notice.entity.NoticeType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterService {

    SseEmitter subscribe();

    void send(String userId, String userRole, NoticeType noticeType, String title, Object data);

    void sendToClient(String userId, String userRole, Object data);

    String getUserKey(String userId, String userRole);
}
