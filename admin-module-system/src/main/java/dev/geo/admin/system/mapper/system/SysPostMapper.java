package dev.geo.admin.system.mapper.system;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.PostPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysPost;

import java.util.List;

/**
 * 岗位信息 数据层
 *
 */
public interface SysPostMapper extends BaseMapperX<SysPost>
{
    default PageResult<SysPost> selectPage(PostPageReqDTO query) {
        return selectPage(query, new LambdaQueryWrapper<SysPost>()
                .like(StrUtil.isNotBlank(query.getPostCode()), SysPost::getPostCode, query.getPostCode())
                .like(StrUtil.isNotBlank(query.getPostName()), SysPost::getPostName, query.getPostName())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysPost::getStatus, query.getStatus())
                .orderByDesc(SysPost::getCreateTime)
                .orderByDesc(SysPost::getId));
    }
    /**
     * 查询岗位数据集合
     * 
     * @param post 岗位信息
     * @return 岗位数据集合
     */
    default List<SysPost> selectPostList(SysPost query) {
        return selectList(new LambdaQueryWrapper<SysPost>()
                .like(StrUtil.isNotBlank(query.getPostCode()), SysPost::getPostCode, query.getPostCode())
                .like(StrUtil.isNotBlank(query.getPostName()), SysPost::getPostName, query.getPostName())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysPost::getStatus, query.getStatus())
                .orderByDesc(SysPost::getCreateTime)
                .orderByDesc(SysPost::getId));
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    default List<SysPost> selectPostAll() {
        return selectList(new LambdaQueryWrapper<SysPost>()
                .orderByAsc(SysPost::getPostSort)
                .orderByDesc(SysPost::getId));
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    default SysPost selectPostById(Long id) {
        return selectById(id);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    List<SysPost> selectPostsByUserName(String userName);

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    default int deletePostById(Long id) {
        return deleteById(id);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    default int deletePostByIds(Long[] ids) {
        return deleteByIds(List.of(ids));
    }

    /**
     * 修改岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    default int updatePost(SysPost post) {
        return updateById(post);
    }

    /**
     * 新增岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    default int insertPost(SysPost post) {
        return insert(post);
    }

    /**
     * 校验岗位名称
     * 
     * @param postName 岗位名称
     * @return 结果
     */
    default SysPost checkPostNameUnique(String postName) {
        return selectOne(SysPost::getPostName, postName);
    }

    /**
     * 校验岗位编码
     * 
     * @param postCode 岗位编码
     * @return 结果
     */
    default SysPost checkPostCodeUnique(String postCode) {
        return selectOne(SysPost::getPostCode, postCode);
    }
}
