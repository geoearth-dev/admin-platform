package dev.geo.admin.system.service.auth;

import com.alibaba.fastjson2.JSONObject;
import dev.geo.admin.common.core.model.ApiResult;

/**
 * 接收认证平台推送的数据
 */
public interface SysSyncDataService {
    public ApiResult<Void> syncData(JSONObject jsonObject);
}
