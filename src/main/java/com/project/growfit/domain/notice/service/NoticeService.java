package com.project.growfit.domain.notice.service;

import com.project.growfit.domain.notice.dto.request.NoticeRequestDto;
import com.project.growfit.domain.notice.dto.response.NoticeListResponseDto;
import com.project.growfit.domain.notice.dto.response.NoticeResponseDto;
import com.project.growfit.domain.notice.entity.Notice;
import com.project.growfit.domain.notice.entity.NoticeType;
import java.util.List;

public interface NoticeService {

    Notice saveParentToChild(NoticeRequestDto dto, NoticeType noticeType);

    NoticeResponseDto getNotice(Long noticeId);

    List<NoticeListResponseDto> getNotices();
}
