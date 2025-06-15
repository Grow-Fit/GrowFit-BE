package com.project.growfit.domain.User.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.project.growfit.domain.User.dto.request.AuthParentRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ChildQrCodeResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.User.service.AuthParentService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthParentServiceImpl implements AuthParentService {
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final AuthenticatedUserProvider authenticatedProvider;

    @Override
    public ResultResponse<?> registerChild(CustomUserDetails user, AuthParentRequestDto request) {
        log.info("[registerChild] 자녀 등록 요청: user_id={}, child_name={}", user.getUserId(), request.child_name());
        Parent parent = getParent(user);
        Child child = createChild(request);
        boolean childExists = parent.hasChildWithName(child.getName());

        if (childExists) {
            log.warn("[registerChild] 중복된 자녀 등록 시도: parent_id={}, child_name={}", parent.getId(), request.child_name());
            throw new BusinessException(ErrorCode.CHILD_ALREADY_EXISTS);
        }

        updateNickname(request, parent);
        parent.addChild(child);
        childRepository.save(child);
        ChildInfoResponseDto dto = ChildInfoResponseDto.toDto(child);

        log.info("[registerChild] 자녀 등록 완료: child_id={}, parent_id={}", child.getId(), parent.getId());
        return new ResultResponse<>(ResultCode.SIGNUP_SUCCESS, dto);
    }

    @Override
    public ResultResponse<?> createQR(CustomUserDetails user) throws WriterException {
        int width = 200;
        int height = 200;
        String uniqueCode = UUID.randomUUID().toString();

        Child child = authenticatedProvider.getAuthenticatedChild();
        Long id = child.getId();

        log.info("[createQR] QR 코드 생성 요청: user_id={}", user.getUserId());
        String url = "http://localhost:8080/api/child/register/" + id + "/credentials";

        if(child.getCodeNumber() != null){
            throw  new BusinessException(ErrorCode.QR_ALREADY_EXISTS);
        }

        child.updateCode(uniqueCode);
        BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(encode, "PNG", out);
            String base64QrCode = Base64.getEncoder().encodeToString(out.toByteArray());
            log.info("[createQR] QR 코드 생성 완료: child_id={}, qr_code={}", id, base64QrCode);
            ChildQrCodeResponseDto dto = ChildQrCodeResponseDto.toDto(child, base64QrCode, uniqueCode);

            return new ResultResponse<>(ResultCode.QR_GENERATION_SUCCESS, dto);
        }catch (Exception e){
            log.warn("[createQR]QR Code OutputStream 도중 Exception 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.QR_GENERATION_FAILED);
        }
    }

    @Transactional
    protected Child createChild(AuthParentRequestDto request){
        log.info("[createChild] 자녀 객체 생성: child_name={}, gender={}, age={}, height={}, weight={}",
                request.child_name(), request.child_gender(), request.child_age(), request.child_height(), request.child_weight());

        return new Child(
                null,
                request.child_name(),
                request.child_gender(),
                request.child_age(),
                request.child_height(),
                request.child_weight(),
                null,
                ROLE.fromString("CHILD"));
    }

    @Transactional
    protected Parent getParent(CustomUserDetails user) {
        return parentRepository.findByEmail(user.getUserId())
                .orElseThrow(() -> {
                    log.warn("[registerChild] 부모 정보 조회 실패: 존재하지 않는 사용자 user_id={}", user.getUserId());
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }
    @Transactional
    protected Child getChild(Long child_id) {
        return childRepository.findById(child_id)
                .orElseThrow(() -> {
                    log.warn("[createQR] 아이 정보 조회 실패: 존재하지 않는 아이 child_id={}", child_id);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }

    private static void updateNickname(AuthParentRequestDto request, Parent parent) {
        parent.updateNickname(request.nickname());
    }
}
