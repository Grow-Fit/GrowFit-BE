package com.project.growfit.domain.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {

    @NotBlank(message = "내용을 작성해주세요.")
    private String message;
}
