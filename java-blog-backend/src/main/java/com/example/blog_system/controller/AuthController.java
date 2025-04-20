package com.example.blog_system.controller;

import com.example.blog_system.dto.ApiResponse;
import com.example.blog_system.dto.LoginRequest;
import com.example.blog_system.model.User;
import com.example.blog_system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 认证控制器
 * 处理登录、登出和验证码相关的请求
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {
        log.info("用户登录: {}", loginRequest.getUsername());
        return authService.login(loginRequest, request);
    }

    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        log.info("用户登出");
        return authService.logout(request);
    }

    /**
     * 生成验证码
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @throws IOException 如果写入响应流失败
     */
    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_JPEG_VALUE)
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应类型
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // 生成验证码图片
        BufferedImage image = authService.generateCaptcha(request);
        
        // 将图片写入响应流
        ImageIO.write(image, "jpg", response.getOutputStream());
    }
}
