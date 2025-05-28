package com.project.growfit.domain.notice.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter parentSave(String parentEmail, SseEmitter emitter) {
        emitters.put("parent:" + parentEmail, emitter);
        return emitter;
    }

    public SseEmitter childSave(String childId, SseEmitter emitter) {
        emitters.put("child:" + childId, emitter);
        return emitter;
    }

    public void deleteEmitterByParentEmail(String parentEmail) {
        emitters.remove("parent:" + parentEmail);
    }

    public void deleteEmitterByChildId(String childId) {
        emitters.remove("child:" + childId);
    }

    public void deleteEmitterByKey(String userKey) {
        emitters.remove(userKey);
    }

    public SseEmitter findEmitterByKey(String userKey) {
        return emitters.get(userKey);
    }
}
