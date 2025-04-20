package com.example.blog_system.service;

import com.example.blog_system.dto.ApiResponse;
import com.example.blog_system.dto.LoginRequest;
import com.example.blog_system.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    ApiResponse<User> login(LoginRequest loginRequest, HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 登出结果
     */
    ApiResponse<Void> logout(HttpServletRequest request);

    /**
     * 生成验证码图片
     *
     * @param request HTTP请求
     * @return 验证码图片
     */
    BufferedImage generateCaptcha(HttpServletRequest request);

    /**
     * 验证验证码
     *
     * @param captchaCode 用户输入的验证码
     * @param request HTTP请求
     * @return 验证结果
     */
    boolean validateCaptcha(String captchaCode, HttpServletRequest request);
}
