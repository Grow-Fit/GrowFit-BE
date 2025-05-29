package com.project.growfit.domain.notice.dto.response;

import com.project.growfit.domain.notice.entity.Notice;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListResponseDto {

    private Long noticeId;
    private String message;
    private String noticeType;
    private String createdAt;
    private Boolean isRead;

    public static NoticeListResponseDto from(Notice notice) {
        NoticeListResponseDto dto = new NoticeListResponseDto();
        dto.setNoticeId(notice.getId());
        dto.setMessage(notice.getMessage());
        dto.setNoticeType(notice.getNoticeType().name());
        dto.setCreatedAt(dto.getFormattedDate(notice.getCreatedAt()));
        dto.setIsRead(notice.isRead());
        return dto;
    }

    public String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }
}
