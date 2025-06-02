package com.project.growfit.domain.notice.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterService {

    SseEmitter subscribe();

    void sendToClient(String userId, String userRole, Object data);

    String getUserKey(String userId, String userRole);
}
