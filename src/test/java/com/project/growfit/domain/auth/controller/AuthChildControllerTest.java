/*
package com.project.growfit.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.auth.controller.AuthChildController;
import com.project.growfit.domain.auth.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.auth.service.AuthChildService;
import com.project.growfit.domain.auth.service.impl.AuthChildServiceImpl;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthChildController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthChildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthChildService authChildService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[registerChildByCode 성공 테스트] - 코드로 자녀 등록")
    void registerChildByCode_Success() throws Exception {
        String code = "testCode";
        when(authChildService.findByCode(code))
                .thenReturn(new ResultResponse<>(ResultCode.INFO_SUCCESS, null));

        mockMvc.perform(get("/api/child/register/code")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.INFO_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[registerChildCredentials 성공 테스트] - 자녀 계정 등록")
    void registerChildCredentials_Success() throws Exception {
        Long childId = 1L;
        AuthChildRequestDto request = new AuthChildRequestDto("childTestId", "password123", "민준콩");

        when(authChildService.registerChildCredentials(anyLong(), any(AuthChildRequestDto.class)))
                .thenReturn(new ResultResponse<>(ResultCode.INFO_REGISTRATION_SUCCESS, null));

        mockMvc.perform(post("/api/child/register/{child_id}/credentials", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.INFO_REGISTRATION_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[loginChild 성공 테스트] - 자녀 로그인")
    void loginChild_Success() throws Exception {
        AuthChildRequestDto request = new AuthChildRequestDto("childTestId", "password123", "민준콩");

        when(authChildService.login(any(AuthChildRequestDto.class), any(HttpServletResponse.class)))
                .thenReturn(new ResultResponse<>(ResultCode.LOGIN_SUCCESS, null));

        mockMvc.perform(post("/api/child/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.LOGIN_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[logoutChild 성공 테스트] - 자녀 로그아웃")
    void logoutChild_Success() throws Exception {
        when(authChildService.logout(any(HttpServletResponse.class)))
                .thenReturn(new ResultResponse<>(ResultCode.LOGOUT_SUCCESS, null));

        mockMvc.perform(post("/api/child/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.LOGOUT_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("[findChildIdByCode 성공 테스트] - 코드로 자녀 ID 찾기")
    void findChildIdByCode_Success() throws Exception {
        String code = "testCode";
        when(authChildService.findChildID(code))
                .thenReturn(new ResultResponse<>(ResultCode.INFO_SUCCESS, null));

        mockMvc.perform(get("/api/child/find/id")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultCode.INFO_SUCCESS.getMessage()));
    }
}*/
