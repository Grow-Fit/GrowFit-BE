/*
package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
@ExtendWith(SpringExtension.class)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OauthService oauthService;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("[getKaKaoAuthorizeCode 성공 테스트] code 파라미터가 있을 때 302 리다이렉트")
    void getKaKaoAuthorizeCode_Success() throws Exception {
        // Given
        String testCode = "test_auth_code";
        String testAccessToken = "test_access_token";

        // When
        when(oauthService.getKakaoAccessToken(anyString())).thenReturn(testAccessToken);

        // Then
        mockMvc.perform(get("/oauth/callback/kakao")
                        .param("code", testCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}
*/
