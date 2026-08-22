package dev.geoearth.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.exception.BusinessException;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.mapper.UserMapper;
import dev.geoearth.admin.model.converter.UserConverter;
import dev.geoearth.admin.model.dto.UserPageQuery;
import dev.geoearth.admin.model.entity.User;
import dev.geoearth.admin.model.vo.UserVO;
import dev.geoearth.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public PageResponse<UserVO> pageUsers(PageParam pageParam, UserPageQuery query) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        // 按名称模糊查询
        wrapper.like(StringUtils.hasText(query.getName()), User::getName, query.getName());
        // 创建时间倒序
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> page = Page.of(pageParam.getPageNum(), pageParam.getPageSize());
        IPage<User> result = userMapper.selectPage(page, wrapper);
        IPage<UserVO> voPage = result.convert(UserConverter::toVO);
        return PageResponse.of(voPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getUserById(Long id) {
        User user = userMapper.selectUserDetailById(id);

        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        return UserConverter.toVO(user);
    }

    @Override
    public List<UserVO> listUsersCreatedAfter(LocalDateTime createdAt) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        // 按名称模糊查询
        wrapper.gt(User::getCreatedAt, createdAt);
        // 创建时间倒序
        wrapper.orderByDesc(User::getCreatedAt);

        List<User> result = userMapper.selectList(wrapper);
        List<UserVO> voPage = result.stream().map(UserConverter::toVO).toList();
        return voPage;
    }
}