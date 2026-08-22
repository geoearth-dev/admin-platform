package dev.geoearth.admin.service;

import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.model.dto.UserPageQuery;
import dev.geoearth.admin.model.vo.UserVO;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    /**
     * 分页查询用户
     */
    PageResponse<UserVO> pageUsers(PageParam pageParam, UserPageQuery query);

    /**
     * 根据ID查询用户
     */
    UserVO getUserById(Long id);

    List<UserVO> listUsersCreatedAfter(LocalDateTime createdAt);

}