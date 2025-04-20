package com.example.blog_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的users表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID，主键，自增
     */
    private Long id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 创建时间，自动生成
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间，自动更新
     */
    private LocalDateTime updatedAt;
}
