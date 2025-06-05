package com.project.growfit.global.converter;

import com.project.growfit.domain.notice.entity.TargetType;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;

public class RoleConverter {

    public static String toRole(TargetType targetType) {
        if (targetType == TargetType.PARENT) {
            return "ROLE_PARENT";
        } else if (targetType == TargetType.CHILD) {
            return "ROLE_CHILD";
        } else {
            throw new BusinessException(ErrorCode.WRONG_USER_ROLE);
        }
    }

    public static TargetType toTargetType(String role) {
        if ("ROLE_PARENT".equals(role)) {
            return TargetType.PARENT;
        } else if ("ROLE_CHILD".equals(role)) {
            return TargetType.CHILD;
        } else {
            throw new BusinessException(ErrorCode.WRONG_USER_ROLE);
        }
    }
}
