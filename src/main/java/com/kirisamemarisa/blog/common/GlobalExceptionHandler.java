package com.kirisamemarisa.blog.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 参数校验异常统一处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        String msg = errors.isEmpty() ? "参数校验失败" : String.join(", ", errors);
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), msg, null);
    }

    // 其他已知业务异常（可自定义异常类）
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    }

    // 未知异常统一处理
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        logger.error("服务器内部错误", ex);
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误", null);
    }
}
