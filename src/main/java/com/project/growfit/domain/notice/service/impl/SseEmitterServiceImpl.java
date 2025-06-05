package com.project.growfit.domain.notice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.notice.entity.NoticeType;
import com.project.growfit.domain.notice.repository.EmitterRepository;
import com.project.growfit.domain.notice.service.SseEmitterService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterServiceImpl implements SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String PARENT_USER_ROLE = "ROLE_PARENT";
    private static final String CHILD_USER_ROLE = "ROLE_CHILD";
    private final EmitterRepository emitterRepository;

    @Override
    @Transactional
    public SseEmitter subscribe() {
        String userId = getUserDetails().getUserId();
        String userRole = getUserDetails().getRole();
        // sse 유효 시간 만료 시, 클라이언트에서 다시 서버로 이벤트 구독 시도
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        if (userRole.equals(PARENT_USER_ROLE)) {
            sseEmitter = emitterRepository.parentSave(userId, sseEmitter);  // userKey: parent email

            // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
            sseEmitter.onCompletion(() -> emitterRepository.deleteEmitterByParentEmail(userId));

            // Emitter 유효 시간 만료 시, emitter 삭제. 유효 시간의 만료는 연결된 시간동안 아무런 이벤트가 발생하지 않았음을 의미함.
            sseEmitter.onTimeout(() -> emitterRepository.deleteEmitterByParentEmail(userId));

            // 첫 구독 시, 이벤트를 발생시킴. sse 연결이 이루어진 후, 하나의 데이터로 전송되지 않는다면 sse 시간 만료 후 503 에러 발생
            //sendToClient(userId, userRole, "subscribe event success, userKey: parent:" + userId);
            sendToClient(userId, userRole, Map.of(
                    "type", "SUBSCRIBE_SUCCESS",
                    "message", "알림 구독이 성공적으로 완료되었습니다.",
                    "userKey", "parent:" + userId
            ));
        } else if (userRole.equals(CHILD_USER_ROLE)) {
            sseEmitter = emitterRepository.childSave(userId, sseEmitter);  // userKey: child user Id
            sseEmitter.onCompletion(() -> emitterRepository.deleteEmitterByChildId(userId));
            sseEmitter.onTimeout(() -> emitterRepository.deleteEmitterByChildId(userId));
            //sendToClient(userId, userRole, "subscribe event success, userKey: child:" + userId);
            sendToClient(userId, userRole, Map.of(
                    "type", "SUBSCRIBE_SUCCESS",
                    "message", "알림 구독이 성공적으로 완료되었습니다.",
                    "userKey", "child:" + userId
            ));
        }
        return sseEmitter;
    }

    /**
     *
     * @param userId: 부모: 이메일, 아이: 아이디 (CustomUserDetails 기준)
     * @param userRole: ROLE_PARENT / ROLE_CHILD
     * @param noticeType
     * @param title: ex) 새로운 편지가 도착했습니다. 이번 미션도 잘했어요~
     * @param data: ex) notice
     */
    @Override
    @Transactional
    public void send(String userId, String userRole, NoticeType noticeType, String title, Object data) {  // userId: 부모: 이메일, 아이: 아이디
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", noticeType.toString());
        payload.put("title", title);
        payload.put("data", data);

        sendToClient(userId, userRole, payload);
    }

    @Override
    public String getUserKey(String userId, String userRole) {
        if (userRole.equals(PARENT_USER_ROLE)) {
            return "parent:" + userId;
        } else if (userRole.equals(CHILD_USER_ROLE)) {
            return "child:" + userId;
        }
        throw new BusinessException(ErrorCode.WRONG_USER_ROLE);
    }

    @Override
    public void sendToClient(String userId, String userRole, Object data) {  // userId: 부모: 이메일, 아이: 아이디
        String userKey = getUserKey(userId, userRole);
        SseEmitter sseEmitter = emitterRepository.findEmitterByKey(userKey);

        if (sseEmitter == null) {
            log.warn("SSE 연결 없음: userKey={}", userKey);
            return;  // 알림 전송 x
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(data);
            sseEmitter.send(
                    SseEmitter.event()
                            .id(userKey)
                            .name("sse")
                            .data(jsonData)
            );
            log.info("알림 전송 성공");
        } catch (JsonProcessingException e){
            log.warn("JSON 변환 오류 발생: ", e);
        } catch (IOException ex) {
            log.warn("클라이언트 연결 끊김(Broken Pipe): userKey={}, error={}", userKey, ex.getMessage());
            emitterRepository.deleteEmitterByKey(userKey);  // 클라이언트 연결 끊김 시 Emitter 제거
        } catch (Exception e) {
            log.error("알림 전송 실패: " + e.getMessage());
            emitterRepository.deleteEmitterByKey(userKey);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
