package com.example.blog_system.dto;

import com.example.blog_system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 包含用户信息和JWT令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private User user;
    private String token;
}
