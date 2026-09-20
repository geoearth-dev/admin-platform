package dev.geo.admin.mybatis.exception;

import dev.geo.admin.common.core.model.ApiResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 数据库约束是并发写入的最后防线，向客户端返回业务提示而非原始 SQL。 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class DatabaseConstraintExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<Void> handleDuplicateKey(DuplicateKeyException exception) {
        return ApiResult.error("数据已存在，请检查账号、编码、名称等唯一字段");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResult<Void> handleDataIntegrity(DataIntegrityViolationException exception) {
        return ApiResult.error("数据不符合约束，请检查必填项、字段长度和状态值");
    }
}
