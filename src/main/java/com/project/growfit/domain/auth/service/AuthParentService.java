package com.project.growfit.domain.auth.service;

import com.google.zxing.WriterException;
import com.project.growfit.domain.auth.dto.request.RegisterChildRequest;
import com.project.growfit.domain.auth.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;

public interface AuthParentService {
    ResultResponse<?> updateParentNickname(CustomUserDetails user, UpdateNicknameRequestDto request);
    ResultResponse<?> registerChild(CustomUserDetails user, RegisterChildRequest request);
    ResultResponse<?> createQR(CustomUserDetails user, Long child_id) throws WriterException;
}
