package com.project.growfit.domain.notice.controller;

import com.project.growfit.domain.notice.dto.response.NoticeResponseDto;
import com.project.growfit.domain.notice.service.NoticeService;
import com.project.growfit.domain.notice.service.SseEmitterService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "SSE 알림", description = "알림 관련 API")
public class SseController {

    private final SseEmitterService sseEmitterService;
    private final NoticeService noticeService;

    @GetMapping(value = "/sse/subscribe", produces = "text/event-stream")
    @Operation(summary = "알림 이벤트 구독",
            description = """
        클라이언트(프론트엔드)에서 서버의 알림 이벤트를 실시간으로 수신받기 위해 구독하는 API입니다.

        이 API는 브라우저에서 EventSource 객체를 통해 요청해야 하며, 연결이 성공되면 서버는 'text/event-stream' 형식으로 알림 이벤트를 지속적으로 전송합니다.

        - 최초 연결 시, 서버는 '구독 완료' 메시지를 보내며 연결이 유지됩니다.
        - 부모(ROLE_PARENT)와 자녀(ROLE_CHILD) 사용자는 각자의 역할에 맞는 알림을 받을 수 있습니다.
        - 서버의 알림 전송 로직은 별도의 트리거(예: 부모가 편지를 작성했을 때)로 동작하며, 이 API는 알림을 구독하는 역할만 합니다.
        - 연결이 끊어지거나 일정 시간 동안 알림이 없으면 서버는 SSE 연결을 종료하며, 클라이언트는 재연결 로직을 구현해야 합니다.

        프론트엔드 개발 시 참고사항:
        - 'subscribe' API를 호출한 후, 서버에서 오는 메시지를 수신하여 알림 UI(예: 편지 아이콘에 빨간 점 표시 등)를 업데이트하세요.
        - 메시지의 형식은 JSON이며, 필요한 경우 'event.data'를 JSON.parse()로 변환하여 사용하세요.
        - 서버에서 전송되는 이벤트 이름(name)은 'sse'로 고정되어 있습니다.
        - 알림 메시지 내용은 추후 서버의 sendToClient 메서드에서 구현됩니다.
        """)
    public SseEmitter subscribe() {
        return sseEmitterService.subscribe();
    }

    @GetMapping("/notice/{noticeId}")
    @Operation(summary = "알림 상세 조회", description = "알림 수신자는 알림 id로 수신 알림을 조회할 수 있다.")
    public ResultResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        NoticeResponseDto dto = noticeService.getNotice(noticeId);
        return new ResultResponse<>(ResultCode.GET_NOTICE_SUCCESS, dto);
    }
}
