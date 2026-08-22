package dev.geoearth.admin.model.converter;

import dev.geoearth.admin.model.entity.User;
import dev.geoearth.admin.model.vo.UserVO;

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