package com.project.growfit.domain.Diet.service;

import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DietService {

    // 1. 음식 검색어로 조회
    ResultResponse<?> searchFoods(CustomUserDetails user, String keyword, int page, int size);

    // 2.  음식 데이터 상세 조회
    ResultResponse<?> getFoodDetail(CustomUserDetails user, Long foodId);
}
