package com.hyn.demos.exception;

import com.hyn.demos.common.BaseResponse;
import com.hyn.demos.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 全局捕获系统异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {BusinessException.class})
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 全局补货运行时异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {RuntimeException.class})
    public BaseResponse<?> businessExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }
}
