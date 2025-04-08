package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.User.entity.Parent;

public record MyInfoResponseDto(
        String nickname,
        String introduce,
        boolean isSelf,
        String profileImageUrl
) {
    public static MyInfoResponseDto from(Parent parent, boolean isSelf) {
        return new MyInfoResponseDto(
                parent.getNickname(),
                parent.getIntroduce(),
                isSelf,
                parent.getPhoto()
        );
    }
}
