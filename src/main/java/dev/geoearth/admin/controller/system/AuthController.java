package dev.geoearth.admin.controller.system;

import dev.geoearth.admin.config.openapi.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = OpenApiTags.SYS_DICT, description = OpenApiTags.SYS_DICT_DESCRIPTION)
public class AuthController {

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
//    @PostMapping("/login")
//    public ApiResult<LoginVo> login(@RequestBody LoginBody loginBody) {
//        // 生成令牌
//        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
//        return ApiResult.success(new LoginVo(token));
//    }

}
