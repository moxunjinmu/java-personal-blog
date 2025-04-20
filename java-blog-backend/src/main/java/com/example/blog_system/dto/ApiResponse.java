package com.example.blog_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应对象
 * 用于规范化HTTP响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .build();
    }

    /**
     * 成功响应（无数据）
     *
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .build();
    }

    /**
     * 失败响应
     *
     * @param code    状态码
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 参数错误响应
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未授权响应
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 禁止访问响应
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 资源不存在响应
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 服务器内部错误响应
     *
     * @param message 错误消息
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
}
