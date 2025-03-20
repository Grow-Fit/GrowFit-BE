package com.project.growfit.global.auto.jwt;

import com.project.growfit.domain.auth.repository.ChildRepository;
import com.project.growfit.domain.auth.repository.ParentRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.redis.entity.TokenRedis;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    private static final String AUTHORITIES_KEY = "role";

    private SecretKey secretKey;
    private final long accessTokenValidityMilliSeconds;
    private final long refreshTokenValidityMilliSeconds;
    private final TokenRedisRepository tokenRedisRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public JwtProvider(@Value("${jwt.secret_key}") String key,
                       @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValiditySeconds,
                       @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValiditySeconds, TokenRedisRepository tokenRedisRepository, ParentRepository parentRepository, ChildRepository childRepository) {
        this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenValidityMilliSeconds = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMilliSeconds = refreshTokenValiditySeconds * 1000;
        this.tokenRedisRepository = tokenRedisRepository;
        this.parentRepository = parentRepository;
        this.childRepository = childRepository;
        log.info("[JwtProvider] JwtProvider 초기화 완료. AccessToken 유효시간: {}ms, RefreshToken 유효시간: {}ms",
                accessTokenValidityMilliSeconds, refreshTokenValidityMilliSeconds);
    }

    public String createAccessToken(String userId, String role, String loginType) {
        String token = createJwt(userId, role, loginType, accessTokenValidityMilliSeconds);
        log.info("[createAccessToken] Access Token 생성 완료 for userId={} with role={}", userId, role);
        return token;
    }

    public String createRefreshToken(String userId) {
        String token = createJwt(userId, "REFRESH", "", refreshTokenValidityMilliSeconds);
        log.info("[createRefreshToken] Refresh Token 생성 완료 for userId={}", userId);
        return token;
    }

    private String createJwt(String userId, String role, String loginType, Long expiredMs) {
        String jwt = Jwts.builder()
                .claim("user_id", userId)
                .claim(AUTHORITIES_KEY, role)
                .claim("login_type", loginType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
        log.debug("[createJwt] JWT 생성: token={}", jwt);
        return jwt;
    }

    public String getUserId(String token) {
        try {
            String userId = Jwts.parser().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody().get("user_id", String.class);
            log.debug("[getUserId] 토큰에서 사용자 ID 추출 성공: {}", userId);
            return userId;
        } catch (ExpiredJwtException e) {
            String userId = e.getClaims().get("user_id", String.class);
            log.debug("[getUserId] 토큰 만료됨. 만료 토큰에서 사용자 ID 추출: {}", userId);
            return userId;
        }
    }

    public String getRole(String token) {
        try {
            String role = Jwts.parser().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody().get(AUTHORITIES_KEY, String.class);
            log.debug("[getRole] 토큰에서 역할 추출 성공: {}", role);
            return role;
        } catch (ExpiredJwtException e) {
            String role = e.getClaims().get("role", String.class);
            log.debug("[getRole] 토큰 만료됨. 만료 토큰에서 역할 추출: {}", role);
            return role;
        }
    }

    public boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody().getExpiration();
            boolean expired = expiration.before(new Date());
            log.debug("[isExpired] 토큰 만료 여부: {}", expired);
            return expired;
        } catch (ExpiredJwtException e) {
            log.debug("[isExpired] 토큰 만료 확인 중 예외 발생(만료된 토큰): {}", e.getMessage());
            return true;
        }
    }

    public UsernamePasswordAuthenticationToken createAuthenticationFromToken(String token) {
        String userId = getUserId(token);
        String role = getRole(token);

        Object user;
        if ("ROLE_PARENT".equals(role)) {
            user = parentRepository.findByEmail(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("부모를 찾을 수 없습니다: " + userId));
        } else {
            user = childRepository.findByChildId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("자식을 찾을 수 없습니다: " + userId));
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);
        log.info("[createAuthenticationFromToken] Authentication 생성: userId={}, 역할={}", userId, role);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UsernamePasswordAuthenticationToken replaceAccessToken(HttpServletResponse response, String token) throws IOException {
        try {
            TokenRedis tokenRedis = tokenRedisRepository.findByAccessToken(token)
                    .orElseThrow(() -> new RuntimeException("다시 로그인 해 주세요."));
            String refreshToken = tokenRedis.getRefreshToken();

            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(refreshToken);

            log.info("[replaceAccessToken] 토큰 재발급 시작");
            String userId = tokenRedis.getId();

            Result result = getResult(userId);


            String newAccessToken = createAccessToken(userId, result.role(), result.login_type());

            tokenRedis.updateAccessToken(newAccessToken);
            tokenRedisRepository.save(tokenRedis);
            log.info("[replaceAccessToken] 토큰 재발급 완료 - 새로운 액세스 토큰 발급됨: {}", newAccessToken);

            saveAccessTokenToCookie(response, newAccessToken);

            return new UsernamePasswordAuthenticationToken(new CustomUserDetails(userId, result.role()), null,
                    List.of(new SimpleGrantedAuthority(result.role())));
        } catch (ExpiredJwtException exception) {
            log.error("리프레시 토큰이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("토큰 재발급 실패", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return null;
    }

    private Result getResult(String userId) {
        String role;
        String login_type;
        if (parentRepository.findByEmail(userId).isPresent()) {
            role = parentRepository.findByEmail(userId).get().getRole().toString();
            login_type = "SOCIAL_KAKAO";
        } else if (childRepository.findByChildId(userId).isPresent()) {
            role = childRepository.findByChildId(userId).get().getRole().toString();
            login_type = "LOCAL";
        } else {
            throw new UsernameNotFoundException("User not found: " + userId);
        }
        return new Result(role, login_type);
    }

    private record Result(String role, String login_type) {
    }

    public void saveAccessTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge((int) (accessTokenValidityMilliSeconds / 1000));
        response.addCookie(cookie);
        response.setHeader("Set-Cookie", "accessToken=" + token + "; Path=/; HttpOnly; Secure; SameSite=None");

        log.info("[saveAccessTokenToCookie] Access Token이 쿠키에 저장되었습니다.");
    }

    public String getSubjectFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("[getSubjectFromToken] 토큰에서 subject 추출 성공: {}", claims.getSubject());
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.debug("[getSubjectFromToken] 만료된 토큰에서 subject 추출: {}", e.getClaims().getSubject());
            return e.getClaims().getSubject();
        }
    }
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    log.debug("[getAccessTokenFromCookie] 쿠키에서 액세스 토큰 추출됨: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        log.debug("[getAccessTokenFromCookie] 쿠키에 액세스 토큰이 존재하지 않습니다.");
        return null;
    }

}