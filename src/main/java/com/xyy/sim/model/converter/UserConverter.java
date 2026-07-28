package com.xyy.sim.model.converter;

import com.xyy.sim.model.entity.User;
import com.xyy.sim.model.vo.UserVO;

public final class UserConverter {

    private UserConverter() {
    }

    public static UserVO toVO(User user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}