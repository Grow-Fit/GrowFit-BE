package com.project.growfit.domain.User.service.impl;

import com.project.growfit.domain.User.dto.request.ChildCredentialsRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.User.dto.response.ChildIdResponse;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.service.AuthChildService;
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
        Long childPid = child.getId();

        log.info("[findByCode] 아이 정보 PID 조회 성공: {}", childPid);
        return new ResultResponse<>(ResultCode.CHILD_INFO_RETRIEVAL_SUCCESS, new ChildIdResponse(childPid));
    }

    public ResultResponse<?> updateNickname(Long child_id, UpdateNicknameRequestDto request) {
        Child child = getChild(child_id);
        child.updateNickname(request.nickname());

        log.info("[updateNickname] 닉네임 변경 완료: child_id={}, new_nickname={}", child_id, request.nickname());
        return new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null);
    }

    @Override
    public ResultResponse<?> registerChildCredentials(Long child_id, ChildCredentialsRequestDto request) {
        log.info("[registerChildCredentials] 아이 계정 정보 등록 요청: child_id={}, child_login_id={}", child_id, request.childId());
        boolean isExists = childRepository.existsByLoginIdOrPassword(request.childId(), request.childPassword());

        if (isExists) {
            throw new BusinessException(ErrorCode.CHILD_ALREADY_EXISTS);
        }
        else {
            Child child = getChild(child_id);
            child.updateCredentials(request.childId(), passwordEncoder.encode(request.childPassword()), request.nickname());
            childRepository.save(child);
        }

        log.info("[registerChildCredentials] 아이 계정 정보 등록 완료: child_id={}", child_id);
        return new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null);
    }


    @Override
    public ResultResponse<?> login(ChildCredentialsRequestDto request, HttpServletResponse response) {
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

        String newAccessToken = jwtProvider.createAccessToken(child.getLoginId(), child.getRole().toString(), "LOCAL");
        String newRefreshToken = jwtProvider.createRefreshToken(child.getLoginId());

        tokenRedisRepository.save(new TokenRedis(child.getLoginId(), newAccessToken, newRefreshToken));
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
        boolean isExist = childRepository.existsByCodeNumberAndLoginId(request.code(), request.user_id());
        if (!isExist) {
            log.warn("[findChildPassword] 비밀번호 변경 실패: 존재하지 않는 사용자 user_id={}", request.user_id());
            throw new BusinessException(ErrorCode.CHILD_NOT_FOUND);
        }
        Child child = getChild(request.code());
        child.updatePassword(passwordEncoder.encode(request.new_password()));

        childRepository.save(child);

        log.info("[findChildPassword] 비밀번호 변경 완료: user_id={}", request.user_id());
        return new ResultResponse<>(ResultCode.CHILD_REGISTRATION_SUCCESS, null);
    }

    private Child getChild(Long child_id) {
        return childRepository.findById(child_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

    private Child getChildByChildId(String login_id) {
        return childRepository.findByLoginId(login_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }


    private Child getChild(String code) {
        return  childRepository.findByCodeNumber(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

}
