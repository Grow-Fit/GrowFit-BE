package com.project.growfit.domain.User.service;

import com.google.zxing.WriterException;
import com.project.growfit.domain.User.dto.request.AuthParentRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ChildQrCodeResponseDto;

public interface AuthParentService {
    ChildInfoResponseDto registerChild(AuthParentRequestDto request);
    ChildQrCodeResponseDto createQR() throws WriterException;
}
