package com.project.growfit.domain.User.service;

import com.google.zxing.WriterException;
import com.project.growfit.domain.User.dto.request.AuthParentRequestDto;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;

public interface AuthParentService {
    ResultResponse<?> registerChild(CustomUserDetails user, AuthParentRequestDto request);
    ResultResponse<?> createQR(CustomUserDetails user) throws WriterException;
}
