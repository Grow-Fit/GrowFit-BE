package com.project.growfit.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.User.controller.AuthParentController;
import com.project.growfit.domain.User.dto.request.RegisterChildRequest;
import com.project.growfit.domain.User.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.User.service.AuthParentService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.config.SecurityConfig;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AuthParentController.class, excludeAutoConfiguration = SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthParentService parentService;

    @MockitoBean
    private ParentRepository parentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthParentController authParentController;

    private CustomUserDetails mockUser;

    @BeforeEach
    void setUp() {
        Parent parent = new Parent("parent@example.com", null, null, null, "kakao", ROLE.ROLE_PARENT);
        mockUser = new CustomUserDetails(parent);
    }

    @Test
    @DisplayName("테스트 엔드포인트 응답 확인")
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/parent/test"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("자녀 등록 - 성공")
    void registerChild_Success() throws Exception {
        UpdateNicknameRequestDto request = new UpdateNicknameRequestDto("childName");
        when(parentService.registerChild(any(CustomUserDetails.class), any(RegisterChildRequest.class)))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null));

        mockMvc.perform(post("/api/parent/child")
                        .principal(() -> "parent@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").doesNotExist());
    }
}