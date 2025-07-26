package com.project.growfit.domain.User.service;

import com.project.growfit.domain.User.dto.request.ChildInfoRequestDto;
import com.project.growfit.domain.User.dto.request.ParentInfoRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ParentInfoResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    ParentInfoResponseDto getParentInfo();
    ChildInfoResponseDto getChildInfo();

    void updateParentInfo(ParentInfoRequestDto request, HttpServletResponse response);
    void updateChildInfo(ChildInfoRequestDto request);
}
