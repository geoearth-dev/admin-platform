package com.xyy.sim.service;

import com.xyy.sim.common.response.PageResponse;
import com.xyy.sim.model.dto.UserPageQuery;
import com.xyy.sim.model.dto.common.PageParam;
import com.xyy.sim.model.vo.UserVO;

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