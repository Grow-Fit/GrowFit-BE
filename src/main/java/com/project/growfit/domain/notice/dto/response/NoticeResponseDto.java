package com.project.growfit.domain.notice.dto.response;

import com.project.growfit.domain.notice.entity.Notice;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDto {

    private Long noticeId;
    private String targetName;  // target nickname
    private String message;
    private String noticeType;
    private String createdAt;
    private Boolean isRead;

    public static NoticeResponseDto from(Notice notice, String targetName) {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setNoticeId(notice.getId());
        dto.setTargetName(targetName);
        dto.setMessage(notice.getMessage());
        dto.setNoticeType(notice.getNoticeType().name());
        dto.setCreatedAt(dto.getFormattedDate(notice.getCreatedAt()));
        dto.setIsRead(notice.isRead());
        return dto;
    }

    public String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
