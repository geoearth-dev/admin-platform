package dev.geoearth.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.geoearth.admin.model.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    User selectUserDetailById(@Param("id") Long id);
}
