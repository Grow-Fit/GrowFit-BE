package com.project.growfit.domain.notice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.growfit.domain.notice.repository.EmitterRepository;
import com.project.growfit.domain.notice.service.impl.SseEmitterServiceImpl;
import com.project.growfit.global.exception.BusinessException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class SseEmitterServiceTest {

    @Mock
    private EmitterRepository emitterRepository;

    @InjectMocks
    private SseEmitterServiceImpl sseEmitterService;

    @Test
    @DisplayName("[getUserKey 부모 테스트] 부모의 사용자 키 반환 성공")
    void getUserKey_parentRole() {
        String result = sseEmitterService.getUserKey("abc123", "ROLE_PARENT");
        assertEquals("parent:abc123", result);
    }

    @Test
    @DisplayName("[getUserKey 자녀 테스트] 자녀의 사용자 키 반환 성공")
    void getUserKey_childRole() {
        String result = sseEmitterService.getUserKey("xyz456", "ROLE_CHILD");
        assertEquals("child:xyz456", result);
    }

    @Test
    @DisplayName("[getUserKey 예외 테스트] 키 반환 실패")
    void getUserKey_invalidRole() {
        assertThrows(BusinessException.class, () -> {
            sseEmitterService.getUserKey("user1", "ROLE_UNKNOWN");
        });
    }

    @Test
    @DisplayName("[sendToClient 성공 테스트] 정상적으로 메시지를 전송했을 때, SseEmitter의 send() 메서드 호출됨")
    void sendToClient_success() throws Exception {
        // given
        String userId = "user1";
        String userRole = "ROLE_PARENT";
        String userKey = "parent:user1";
        SseEmitter emitter = mock(SseEmitter.class);

        when(emitterRepository.findEmitterByKey(userKey)).thenReturn(emitter);

        // when
        sseEmitterService.sendToClient(userId, userRole, Map.of("message", "test"));

        // then
        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));  // send()가 호출됐는지만 검증
    }

    @Test
    @DisplayName("[sendToClient 존재 여부 테스트] 해당 사용자에 대한 SseEmitter가 존재하지 않으면 아무 작업도 수행하지 않음")
    void sendToClient_noEmitter() {
        // given
        String userId = "user1";
        String userRole = "ROLE_PARENT";
        String userKey = "parent:user1";

        when(emitterRepository.findEmitterByKey(userKey)).thenReturn(null);

        // when
        sseEmitterService.sendToClient(userId, userRole, Map.of("message", "test"));

        // then
        verify(emitterRepository, never()).deleteEmitterByKey(anyString());
    }

    @Test
    @DisplayName("[sendToClient 예외 시 삭제 테스트] 예외 발생 후 SseEmitter 삭제")
    void sendToClient_sendThrowsIOException() throws Exception {
        // given
        String userId = "user1";
        String userRole = "ROLE_PARENT";
        String userKey = "parent:user1";
        SseEmitter emitter = mock(SseEmitter.class);

        when(emitterRepository.findEmitterByKey(userKey)).thenReturn(emitter);
        doThrow(new IOException("Broken pipe")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));

        // when
        sseEmitterService.sendToClient(userId, userRole, Map.of("message", "test"));

        // then
        verify(emitterRepository).deleteEmitterByKey(userKey);
    }

}