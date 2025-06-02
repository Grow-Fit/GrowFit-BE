package com.project.growfit.domain.notice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ChildGender;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.notice.dto.request.NoticeRequestDto;
import com.project.growfit.domain.notice.dto.response.NoticeListResponseDto;
import com.project.growfit.domain.notice.entity.Notice;
import com.project.growfit.domain.notice.entity.NoticeType;
import com.project.growfit.domain.notice.repository.NoticeRepository;
import com.project.growfit.domain.notice.service.impl.NoticeServiceImpl;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeServiceImpl  noticeService;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    @DisplayName("[saveParentToChild 성공 테스트] 부모가 로그인하여 아이에게 편지 작성")
    void saveParentToChild_success() {
        // given
        Parent parent = new Parent("parent@example.com", "부모님", null, "kakao", "11111111", ROLE.ROLE_PARENT);
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(parentRepository.findByEmail(anyString())).thenReturn(Optional.of(parent));

        Child child = new Child("child1", "encodedPassword", "아이", ChildGender.MALE, 10, 140, 35, "아이 닉네임", ROLE.ROLE_CHILD);
        parent.getChildren().add(child);

        NoticeRequestDto dto = new NoticeRequestDto();
        dto.setMessage("내용");

        // when
        Notice notice = noticeService.saveParentToChild(dto, NoticeType.PRAISE_LETTER);

        // then
        assertNotNull(notice);
        verify(noticeRepository).save(any(Notice.class));
    }

    @Test
    @DisplayName("[getNotice 성공 테스트] 아이가 로그인하여 편지 조회")
    void getNotice_success() {
        // given
        Child child = new Child("child1", "encodedPassword", "아이", ChildGender.MALE, 10, 140, 35, "아이 닉네임", ROLE.ROLE_CHILD);
        ReflectionTestUtils.setField(child, "id", 1L);

        CustomUserDetails userDetails = new CustomUserDetails(child);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(childRepository.findByLoginId(anyString())).thenReturn(Optional.of(child));

        NoticeRequestDto dto1 = new NoticeRequestDto();
        dto1.setMessage("내용1");
        NoticeRequestDto dto2 = new NoticeRequestDto();
        dto2.setMessage("내용2");
        NoticeRequestDto dto3 = new NoticeRequestDto();
        dto3.setMessage("내용3");

        Notice notice1 = Notice.createNoticeParentToChild(dto1, child, NoticeType.PRAISE_LETTER);
        Notice notice2 = Notice.createNoticeParentToChild(dto2, child, NoticeType.PRAISE_LETTER);
        Notice notice3 = Notice.createNoticeParentToChild(dto3, child, NoticeType.PRAISE_LETTER);

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(notice1, "createdAt", now.minusDays(2));
        ReflectionTestUtils.setField(notice2, "createdAt", now.minusDays(1));
        ReflectionTestUtils.setField(notice3, "createdAt", now);

        List<Notice> notices = List.of(notice1, notice2, notice3);
        when(noticeRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(any(), eq(child.getId()))).thenReturn(notices);

        // when
        List<NoticeListResponseDto> result = noticeService.getNotices();

        // then
        assertEquals(3, result.size());
    }
}