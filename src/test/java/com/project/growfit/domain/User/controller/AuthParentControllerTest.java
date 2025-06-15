/*

package com.project.growfit.domain.User.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.User.service.AuthParentService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
}
*/
