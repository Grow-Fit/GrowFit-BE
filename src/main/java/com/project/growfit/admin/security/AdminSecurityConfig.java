package com.project.growfit.admin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class AdminSecurityConfig {

    private final AdminDetailService adminDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .loginPage("/admin/login") // 로그인 페이지
                        .loginProcessingUrl("/admin/login-process") // 실제 인증 처리
                        .defaultSuccessUrl("/admin/main", true) // 로그인 성공 시 리다이렉트
                        .failureUrl("/admin/login?error=true") // 로그인 실패 시
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/login-process").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                )
                .userDetailsService(adminDetailsService); // 여기서 등록!

        return http.build();
    }
}
