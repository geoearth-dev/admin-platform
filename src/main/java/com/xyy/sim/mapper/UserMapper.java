package com.xyy.sim.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyy.sim.model.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    User selectUserDetailById(@Param("id") Long id);
}
