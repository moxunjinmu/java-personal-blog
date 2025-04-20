package com.example.blog_system.service.impl;

import com.example.blog_system.dto.ApiResponse;
import com.example.blog_system.dto.LoginRequest;
import com.example.blog_system.dto.LoginResponse;
import com.example.blog_system.dto.RegisterRequest;
import com.example.blog_system.model.User;
import com.example.blog_system.service.AuthService;
import com.example.blog_system.service.UserService;
import com.example.blog_system.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    private static final String CAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletRequest request) {
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

            // 密码验证成功，创建已认证的令牌
            // 注意：这里不使用 authenticationManager.authenticate() 方法
            // 因为它会触发另一轮认证，导致循环调用
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(), 
                    null, // 已验证通过，不需要密码
                    java.util.Collections.emptyList() // 暂时不设置权限
                );
            
            // 保存认证信息到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername());
            
            // 清除敏感信息
            user.setPassword(null);
            user.setSalt(null);
            
            // 创建登录响应，包含用户信息和令牌
            LoginResponse loginResponse = new LoginResponse(user, token);
            
            return ApiResponse.success(loginResponse);
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
    
    @Override
    public ApiResponse<User> register(RegisterRequest registerRequest, HttpServletRequest request) {
        log.info("开始处理用户注册请求: {}", registerRequest.getUsername());
        
        // 验证验证码
        if (!validateCaptcha(registerRequest.getCaptchaCode(), request)) {
            log.warn("验证码验证失败: {}", registerRequest.getUsername());
            return ApiResponse.badRequest("验证码错误或已过期");
        }
        
        try {
            // 检查用户名是否已存在
            log.debug("检查用户名是否存在: {}", registerRequest.getUsername());
            User existingUser = userService.findByUsername(registerRequest.getUsername());
            if (existingUser != null) {
                log.warn("用户名已存在: {}", registerRequest.getUsername());
                return ApiResponse.badRequest("用户名已存在");
            }
            
            // 检查密码与确认密码是否一致
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                log.warn("密码与确认密码不一致: {}", registerRequest.getUsername());
                return ApiResponse.badRequest("两次输入的密码不一致");
            }
            
            // 创建新用户
            log.debug("创建新用户对象: {}", registerRequest.getUsername());
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setEmail(registerRequest.getEmail());
            
            // 生成盐值并加密密码
            log.debug("生成盐值并加密密码: {}", registerRequest.getUsername());
            String salt = userService.generateSalt();
            String encodedPassword = userService.encodePassword(registerRequest.getPassword(), salt);
            
            newUser.setSalt(salt);
            newUser.setPassword(encodedPassword);
            newUser.setCreatedAt(java.time.LocalDateTime.now());
            newUser.setUpdatedAt(java.time.LocalDateTime.now());
            
            // 设置默认昵称和头像
            newUser.setNickname(registerRequest.getUsername());
            newUser.setAvatar("/images/default-avatar.jpg");
            
            // 保存用户
            log.info("开始保存用户到数据库: {}", registerRequest.getUsername());
            try {
                User savedUser = userService.save(newUser);
                log.info("用户注册成功: {}, ID: {}", savedUser.getUsername(), savedUser.getId());
                
                // 清除敏感信息
                savedUser.setPassword(null);
                savedUser.setSalt(null);
                
                return ApiResponse.success(savedUser);
            } catch (Exception e) {
                log.error("保存用户到数据库失败: {}, 错误: {}", registerRequest.getUsername(), e.getMessage(), e);
                throw new RuntimeException("保存用户失败: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("注册处理过程中发生异常: {}, 错误: {}", registerRequest.getUsername(), e.getMessage(), e);
            return ApiResponse.serverError("注册失败: " + e.getMessage());
        }
    }
}
