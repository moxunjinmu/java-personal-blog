package com.example.blog_system.exception;

import com.example.blog_system.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数校验失败: {}", errorMessage);
        return ApiResponse.badRequest(errorMessage);
    }

    /**
     * 处理绑定异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindExceptions(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数绑定失败: {}", errorMessage);
        return ApiResponse.badRequest(errorMessage);
    }

    /**
     * 处理认证异常
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Void> handleAuthenticationException(AuthenticationException ex) {
        log.warn("认证失败: {}", ex.getMessage());
        
        if (ex instanceof BadCredentialsException) {
            return ApiResponse.unauthorized("用户名或密码错误");
        }
        
        return ApiResponse.unauthorized("认证失败: " + ex.getMessage());
    }

    /**
     * 处理授权异常
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("访问被拒绝: {}", ex.getMessage());
        return ApiResponse.forbidden("没有权限访问此资源");
    }

    /**
     * 处理其他所有异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAllExceptions(Exception ex) {
        log.error("服务器内部错误", ex);
        return ApiResponse.serverError("服务器内部错误，请稍后重试");
    }
}
