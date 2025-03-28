package com.project.growfit.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.auth.dto.request.ChildCredentialsRequest;
import com.project.growfit.domain.auth.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.auth.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.auth.service.AuthChildService;
import com.project.growfit.global.auto.jwt.excpetion.CustomAuthenticationEntryPoint;
import com.project.growfit.global.config.SecurityConfig;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AuthChildController.class, excludeAutoConfiguration = SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthChildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthChildService authChildService;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mockito 초기화는 필요 없음 (@WebMvcTest가 알아서 해줌)
    }

    @Test
    @DisplayName("[registerChildByCode 성공 테스트] - 코드로 자녀 등록")
    void registerChildByCode_Success() throws Exception {
        String code = "testCode";
        when(authChildService.findByCode(code))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, null));

        mockMvc.perform(get("/api/child/register/code")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[registerChildCredentials 성공 테스트] - 자녀 계정 등록")
    void registerChildCredentials_Success() throws Exception {
        Long childId = 1L;
        ChildCredentialsRequest request = new ChildCredentialsRequest("childTestId", "password123");

        when(authChildService.registerChildCredentials(anyLong(), any(ChildCredentialsRequest.class)))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null));

        mockMvc.perform(post("/api/child/register/{child_id}/credentials", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.CHILD_REGISTRATION_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[registerChildPreferences 성공 테스트] - 자녀 닉네임 등록")
    void registerChildPreferences_Success() throws Exception {
        Long childId = 1L;
        UpdateNicknameRequestDto request = new UpdateNicknameRequestDto("newNickname");

        when(authChildService.updateNickname(anyLong(), any(UpdateNicknameRequestDto.class)))
                .thenReturn(new ResultResponse<>(ResultCode.PARENT_NICKNAME_SET_SUCCESS, null));

        mockMvc.perform(put("/api/child/{child_id}/nickname", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.PARENT_NICKNAME_SET_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[loginChild 성공 테스트] - 자녀 로그인")
    void loginChild_Success() throws Exception {
        ChildCredentialsRequest request = new ChildCredentialsRequest("childTestId", "password123");

        when(authChildService.login(any(ChildCredentialsRequest.class), any(HttpServletResponse.class)))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_LOGIN_SUCCESS, null));

        mockMvc.perform(post("/api/child/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.CHILD_LOGIN_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[findChildIdByCode 성공 테스트] - 코드로 자녀 ID 찾기")
    void findChildIdByCode_Success() throws Exception {
        String code = "testCode";
        when(authChildService.findChildID(code))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, null));

        mockMvc.perform(get("/api/child/find/id")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[resetChildPassword 성공 테스트] - 자녀 비밀번호 재설정")
    void resetChildPassword_Success() throws Exception {
        FindChildPasswordRequestDto request = new FindChildPasswordRequestDto("childTestId", "testCode", "newPassword");

        when(authChildService.findChildPassword(any(FindChildPasswordRequestDto.class)))
                .thenReturn(new ResultResponse<>(ResultCode.CHILD_LOGIN_SUCCESS, null));

        mockMvc.perform(post("/api/child/find/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.CHILD_LOGIN_SUCCESS.getMessage()));
    }
}