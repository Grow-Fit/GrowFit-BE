package com.project.growfit.domain.User.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.User.dto.request.ChildInfoRequestDto;
import com.project.growfit.domain.User.dto.request.ParentInfoRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ParentInfoResponseDto;
import com.project.growfit.domain.User.entity.ChildGender;
import com.project.growfit.domain.User.service.UserService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("부모 정보 조회 API")
    void getParentInfoTest() throws Exception {
        ParentInfoResponseDto responseDto = new ParentInfoResponseDto(
                "엄마", "profile.jpg",
                new ChildInfoResponseDto(1L, "code123", "민준", ChildGender.MALE, 11, null)
        );
        Mockito.when(userService.getParentInfo()).thenReturn(responseDto);

        mockMvc.perform(get("/api/parent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResultCode.INFO_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.data.nickname").value("엄마"));
    }

    @Test
    @DisplayName("아이 정보 조회 API")
    void getChildInfoTest() throws Exception {
        ChildInfoResponseDto dto = new ChildInfoResponseDto(
                1L, "codeABC", "지우", ChildGender.FEMALE, 10, null
        );
        Mockito.when(userService.getChildInfo()).thenReturn(dto);

        mockMvc.perform(get("/api/child"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.child_name").value("지우"));
    }

    @Test
    @DisplayName("부모 정보 수정 API")
    void updateParentInfoTest() throws Exception {
        ParentInfoRequestDto requestDto = new ParentInfoRequestDto(
                "새엄마", "new@example.com", "민준", 12, ChildGender.MALE, 140, 35
        );

        mockMvc.perform(put("/api/parent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 정보가 수정되었습니다."));
    }

    @Test
    @DisplayName("아이 정보 수정 API")
    void updateChildInfoTest() throws Exception {
        ChildInfoRequestDto requestDto = new ChildInfoRequestDto(
                "짱이", "newpw123!", ChildGender.FEMALE, 13, 150L, 45L
        );

        mockMvc.perform(put("/api/child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 정보가 수정되었습니다."));
    }
}