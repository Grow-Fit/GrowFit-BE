package com.project.growfit.domain.User.service.impl;

import com.project.growfit.domain.User.dto.request.ChildInfoRequestDto;
import com.project.growfit.domain.User.dto.request.ParentInfoRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ParentInfoResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ChildGender;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AuthenticatedUserProvider userProvider;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    private Parent mockParent;
    private Child mockChild;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockChild = new Child("child123", "지우", ChildGender.FEMALE, 10, 130, 30, "encodedPw", ROLE.ROLE_CHILD);
        mockParent = new Parent("parent@example.com", "엄마", "photo.jpg", "kakao", "pid", ROLE.ROLE_PARENT);
        mockParent.addChild(mockChild);
    }

    @Test
    @DisplayName("부모 정보 조회 성공")
    void testGetParentInfo() {
        when(userProvider.getAuthenticatedParent()).thenReturn(mockParent);

        ParentInfoResponseDto result = userService.getParentInfo();

        assertThat(result.nickname()).isEqualTo("엄마");
        assertThat(result.child().child_name()).isEqualTo("지우");
    }

    @Test
    @DisplayName("아이 정보 조회 성공")
    void testGetChildInfo() {
        when(userProvider.getAuthenticatedChild()).thenReturn(mockChild);

        ChildInfoResponseDto result = userService.getChildInfo();

        assertThat(result.child_name()).isEqualTo("지우");
    }

    @Test
    @DisplayName("부모 정보 수정 - 이메일 변경 포함")
    void testUpdateParentInfo_withEmailChange() {
        when(userProvider.getAuthenticatedParent()).thenReturn(mockParent);
        ParentInfoRequestDto request = new ParentInfoRequestDto(
                "새엄마", "new@example.com", "지우", 11, ChildGender.FEMALE, 135, 32
        );

        userService.updateParentInfo(request, response);

        verify(jwtProvider).regenerateToken(
                eq("parent@example.com"),
                eq("new@example.com"),
                eq("ROLE_PARENT"),
                eq("SOCIAL_KAKAO"),
                eq(response)
        );
    }

    @Test
    @DisplayName("아이 정보 수정 - 비밀번호 포함")
    void testUpdateChildInfo_withPassword() {
        when(userProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPw");

        ChildInfoRequestDto request = new ChildInfoRequestDto(
                "지우업", "새비번123", ChildGender.FEMALE, 12, 140L, 35L
        );

        userService.updateChildInfo(request);

        verify(passwordEncoder).encode("새비번123");
        assertThat(mockChild.getNickname()).isEqualTo("지우업");
        assertThat(mockChild.getLatestBodyInfo().getHeight()).isEqualTo(140L);
    }
}