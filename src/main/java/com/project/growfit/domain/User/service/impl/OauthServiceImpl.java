package com.project.growfit.domain.User.service.impl;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.project.growfit.domain.User.dto.request.ParentOAuthRequestDto;
import com.project.growfit.domain.User.dto.response.ParentLoginResponseDto;
import com.project.growfit.domain.User.dto.response.ParentResponse;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.User.service.OauthService;
import com.project.growfit.global.auth.cookie.CookieService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.redis.entity.TokenRedis;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {

    private final JwtProvider jwtProvider;
    private final ParentRepository parentRepository;
    private final CookieService cookieService;
    private final AuthenticatedUserProvider authenticatedUser;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenRedisRepository tokenRedisRepository;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    String userInfoReqUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${custom.oauth2.kakao.logout-uri}")
    private String kakaoLogoutUri;


    @Override
    public String getKakaoAccessToken(String code) {
        log.info("[getKakaoAccessToken] 카카오 액세스 토큰 요청: code={}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(kakaoTokenUri, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.getBody());
            log.info("[getKakaoAccessToken] 카카오 액세스 토큰 발급 완료");
            return element.getAsJsonObject().get("access_token").getAsString();
        } else {
            log.error("[getKakaoAccessToken] 카카오 액세스 토큰 요청 실패: {}", response.getStatusCode());
            throw new BusinessException(ErrorCode.OAUTH_ACCESS_TOKEN_ERROR);
        }
    }

    @Override
    public HashMap<String, Object> getUserKakaoInfo(String accessToken) {
        log.info("[getUserKakaoInfo] 카카오 사용자 정보 요청 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoReqUri, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.getBody());

            HashMap<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", element.getAsJsonObject().get("id").getAsString());

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            userInfo.put("nickname", properties.get("nickname").getAsString());

            if (element.getAsJsonObject().get("kakao_account").getAsJsonObject().has("email")) {
                userInfo.put("email", element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString());
            }
            log.info("[getUserKakaoInfo] 카카오 사용자 정보 조회 성공: email={}", userInfo.get("email"));
            return userInfo;
        } else {
            log.error("[getUserKakaoInfo] 카카오 사용자 정보 요청 실패: {}", response.getStatusCode());
            throw new BusinessException(ErrorCode.OAUTH_USER_INFO_ERROR);
        }
    }

    @Override
    public ResultResponse<?> kakaoLogin(String accessToken, HttpServletResponse response) {
        log.info("[kakaoLogin] 카카오 로그인 요청 시작");
        boolean isNewUser = false;
        ParentOAuthRequestDto requestDto = getUserKakaoSignupRequestDto(getUserKakaoInfo(accessToken));
        ParentResponse parentResponse = findByUserKakaoIdentifier(requestDto.id());

        if (parentResponse == null) {
            String email = requestDto.email();
            log.info("[kakaoLogin] 신규 사용자, 회원가입 진행: email={}", email);
            isNewUser = true;
            signUp(requestDto);
            parentResponse = findByUserKakaoIdentifier(requestDto.id());
            cookieService.saveEmailToCookie(response, email);
            if (parentResponse == null) {
                log.error("[kakaoLogin] 회원가입 후 사용자 정보 조회 실패: email={}", requestDto.email());
                throw new BusinessException(ErrorCode.USER_REGISTRATION_FAILED);
            }
            generateAndSaveTokens(response, parentResponse);

            log.info("[kakaoLogin] 신규 사용자 accessToken 저장 완료");
        }
        generateAndSaveTokens(response, parentResponse);

        log.info("[kakaoLogin] 카카오 로그인 성공: email={}, accessToken 저장 완료", parentResponse.email());
        ParentLoginResponseDto dto = new ParentLoginResponseDto(parentResponse.email(), isNewUser);
        return new ResultResponse<>(ResultCode.LOGIN_SUCCESS, dto);
    }

    @Override
    public ResultResponse<String> kakaoLogout(String access_token, HttpServletResponse response) {
        Parent user = authenticatedUser.getAuthenticatedParent();
        tokenRedisRepository.deleteById(user.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(access_token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        cookieService.clearCookie(response, "accessToken");

        try {
            restTemplate.postForEntity(kakaoLogoutUri, request, String.class);
        } catch (HttpClientErrorException e) {
            System.err.println("카카오 로그아웃 실패: " + e.getMessage());
        }
        return ResultResponse.of(ResultCode.LOGOUT_SUCCESS, "");
    }

    @Override
    public ParentResponse findByUserKakaoIdentifier(String kakaoIdentifier) {
        log.info("[findByUserKakaoIdentifier] 카카오 ID로 부모 정보 조회: kakao_id={}", kakaoIdentifier);
        List<Parent> parents = parentRepository.findParentByProviderId(kakaoIdentifier).orElse(List.of());

        if (parents.isEmpty()) {
            log.warn("[findByUserKakaoIdentifier] 부모 정보 없음: kakao_id={}", kakaoIdentifier);
            return null;
        }
        return new ParentResponse(parents.get(0));
    }

    @Override
    @Transactional
    public Long signUp(ParentOAuthRequestDto requestDto) {
        try {
            log.info("[signUp] 부모 회원가입 요청: email={}", requestDto.email());
            Long parentId = parentRepository.save(requestDto.toEntity(requestDto.email(), requestDto.nickname(), requestDto.id())).getId();
            log.info("[signUp] 부모 회원가입 완료: parent_id={}", parentId);
            return parentId;
        } catch (Exception e) {
            log.error("[signUp] 부모 회원가입 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FAILED_TO_SAVE_USER);
        }
    }

    private ParentOAuthRequestDto getUserKakaoSignupRequestDto(HashMap<String, Object> userInfo) {
        return new ParentOAuthRequestDto(
                (String) userInfo.get("email"),
                (String) userInfo.get("nickname"),
                (String) userInfo.get("id")
        );
    }

    private void generateAndSaveTokens(HttpServletResponse response, ParentResponse parentResponse) {
        String accessToken = jwtProvider.createAccessToken(parentResponse.email(), parentResponse.roles(), "SOCIAL_KAKAO");
        String refreshToken = jwtProvider.createRefreshToken(parentResponse.email());
        tokenRedisRepository.save(new TokenRedis(parentResponse.email(), accessToken, refreshToken));
        cookieService.saveAccessTokenToCookie(response, accessToken);
    }
}