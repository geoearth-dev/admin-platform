package dev.geo.admin.quartz.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.model.dto.JobPageReqDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysJobMapper {
    IPage<SysJob> selectJobPage(IPage<SysJob> page, @Param("query") JobPageReqDTO query);
    List<SysJob> selectJobList(@Param("query") JobPageReqDTO query);
    List<SysJob> selectJobAll();
    SysJob selectJobById(@Param("id") Long id);
    int deleteJobById(@Param("id") Long id);
    int updateJob(SysJob job);
    int insertJob(SysJob job);
}
