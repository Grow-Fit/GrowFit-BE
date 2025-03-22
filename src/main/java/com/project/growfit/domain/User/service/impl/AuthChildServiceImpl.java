package com.project.growfit.domain.auth.service.impl;

import com.project.growfit.domain.auth.dto.request.*;
import com.project.growfit.domain.auth.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.auth.entity.Child;
import com.project.growfit.domain.auth.repository.ChildRepository;
import com.project.growfit.domain.auth.service.AuthChildService;
import com.project.growfit.global.auto.jwt.JwtProvider;
import com.project.growfit.global.auto.service.CustomAuthenticationProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.redis.entity.TokenRedis;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthChildServiceImpl implements AuthChildService {
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final CustomAuthenticationProvider authenticationProvider;

    public ResultResponse<?> findByCode(String code) {
        log.info("[findByCode] 코드로 아이 정보 조회 요청: {}", code);
        Child child = getChild(code);
        ChildInfoResponseDto dto = ChildInfoResponseDto.toDto(child);

        log.info("[findByCode] 아이 정보 조회 성공: {}", dto);
        return new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, dto);
    }

    public ResultResponse<?> updateNickname(Long child_id, UpdateNicknameRequestDto request) {
        Child child = getChild(child_id);
        child.updateNickname(request.nickname());

        log.info("[updateNickname] 닉네임 변경 완료: child_id={}, new_nickname={}", child_id, request.nickname());
        return new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null);
    }

    @Override
    public ResultResponse<?> registerChildCredentials(Long child_id, ChildCredentialsRequest request) {
        log.info("[registerChildCredentials] 아이 계정 정보 등록 요청: child_id={}, child_login_id={}", child_id, request.childId());
        Child child = getChild(child_id);
        child.updateCredentials(request.childId(), passwordEncoder.encode(request.childPassword()));
        childRepository.save(child);

        log.info("[registerChildCredentials] 아이 계정 정보 등록 완료: child_id={}", child_id);
        return new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, null);
    }


    @Override
    public ResultResponse<?> login(ChildCredentialsRequest request, HttpServletResponse response) {
        log.info("[login] 아이 로그인 시도 . . . : child_login_id={}", request.childId());
        Authentication authenticate;
        try {
            authenticate = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(request.childId(), request.childPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (Exception e) {
            log.warn("[login] 로그인 실패: child_login_id={}", request.childId());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        log.info("[login] 아이 로그인 성공: child_login_id={}", request.childId());

        Child child = getChildByChildId(request.childId());
        log.info("[login] 아이 정보 조회 완료: {}", child);

        String newAccessToken = jwtProvider.createAccessToken(child.getChildId(), child.getRole().toString(), "LOCAL");
        String newRefreshToken = jwtProvider.createRefreshToken(child.getChildId());

        tokenRedisRepository.save(new TokenRedis(child.getChildId(), newAccessToken, newRefreshToken));
        log.info("[login] 새 AccessToken 및 RefreshToken 저장 완료: child_login_id={}", request.childId());

        jwtProvider.saveAccessTokenToCookie(response, newAccessToken);
        log.info("[login] AccessToken을 쿠키에 저장 완료: child_login_id={}", request.childId());

        return new ResultResponse<>(ResultCode.CHILD_LOGIN_SUCCESS, null);
    }

    public ResultResponse<?> findChildID(String code) {
        log.info("[findChildID] 코드로 아이 ID 찾기 요청: {}", code);
        Child child = getChild(code);
        ChildInfoResponseDto dto = ChildInfoResponseDto.toDto(child);

        log.info("[findChildID] 아이 ID 찾기 성공: {}", dto);
        return new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, dto);
    }

    @Override
    public ResultResponse<?> findChildPassword(FindChildPasswordRequestDto request) {
        log.info("[findChildPassword] 아이 비밀번호 찾기 요청: user_id={}, code={}", request.user_id(), request.code());
        boolean isExist = childRepository.existsByCodeAndChildId(request.code(), request.user_id());
        if (!isExist) {
            log.warn("[findChildPassword] 비밀번호 변경 실패: 존재하지 않는 사용자 user_id={}", request.user_id());
            throw new BusinessException(ErrorCode.CHILD_NOT_FOUND);
        }
        Child child = getChild(request.code());
        child.updatePassword(request.new_password());
        childRepository.save(child);

        log.info("[findChildPassword] 비밀번호 변경 완료: user_id={}", request.user_id());
        return new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null);
    }

    private Child getChild(Long child_id) {
        return childRepository.findByPid(child_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

    private Child getChildByChildId(String child_id) {
        return childRepository.findByChildId(child_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }


    private Child getChild(String code) {
        return  childRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

}
