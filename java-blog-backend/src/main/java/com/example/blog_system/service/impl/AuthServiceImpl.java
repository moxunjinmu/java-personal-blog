package com.example.blog_system.service.impl;

import com.example.blog_system.dto.ApiResponse;
import com.example.blog_system.dto.LoginRequest;
import com.example.blog_system.model.User;
import com.example.blog_system.service.AuthService;
import com.example.blog_system.service.UserService;
import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final Producer captchaProducer;

    private static final String CAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Override
    public ApiResponse<User> login(LoginRequest loginRequest, HttpServletRequest request) {
        // 验证验证码
        if (!validateCaptcha(loginRequest.getCaptchaCode(), request)) {
            return ApiResponse.badRequest("验证码错误或已过期");
        }

        try {
            // 查找用户
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                return ApiResponse.badRequest("用户名或密码错误");
            }

            // 验证密码
            if (!userService.verifyPassword(loginRequest.getPassword(), user.getPassword(), user.getSalt())) {
                return ApiResponse.badRequest("用户名或密码错误");
            }

            // 创建认证令牌
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            
            // 进行认证
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // 保存认证信息到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 清除敏感信息
            user.setPassword(null);
            user.setSalt(null);
            
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("登录失败", e);
            return ApiResponse.serverError("登录失败，请稍后重试");
        }
    }

    @Override
    public ApiResponse<Void> logout(HttpServletRequest request) {
        try {
            // 清除安全上下文
            SecurityContextHolder.clearContext();
            
            // 使会话失效
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("登出失败", e);
            return ApiResponse.serverError("登出失败，请稍后重试");
        }
    }

    @Override
    public BufferedImage generateCaptcha(HttpServletRequest request) {
        // 生成验证码文本
        String capText = captchaProducer.createText();
        
        // 将验证码文本保存到会话中
        HttpSession session = request.getSession(true);
        session.setAttribute(CAPTCHA_SESSION_KEY, capText);
        
        // 生成验证码图片
        return captchaProducer.createImage(capText);
    }

    @Override
    public boolean validateCaptcha(String captchaCode, HttpServletRequest request) {
        if (captchaCode == null || captchaCode.isEmpty()) {
            return false;
        }
        
        // 从会话中获取验证码
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        String sessionCaptcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);
        
        // 验证完成后移除会话中的验证码
        session.removeAttribute(CAPTCHA_SESSION_KEY);
        
        // 比较验证码（忽略大小写）
        return sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(captchaCode);
    }
}
