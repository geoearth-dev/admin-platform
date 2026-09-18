package dev.geo.admin.quartz.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.geo.admin.quartz.model.SysJobLog;
import dev.geo.admin.quartz.model.dto.JobLogPageReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysJobLogMapper {

    IPage<SysJobLog> selectJobLogPage(IPage<SysJobLog> page, @Param("query") JobLogPageReqDTO query);

    List<SysJobLog> selectJobLogList(@Param("query") JobLogPageReqDTO query);

    SysJobLog selectJobLogById(@Param("id") Long id);

    int insertJobLog(SysJobLog jobLog);

    int deleteJobLogByIds(@Param("ids") Long[] ids);

    int deleteJobLogById(@Param("id") Long id);

    void cleanJobLog();
}
