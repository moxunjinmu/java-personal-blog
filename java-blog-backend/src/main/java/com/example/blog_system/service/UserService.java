package com.example.blog_system.service;

import com.example.blog_system.model.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User findById(Long id);

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User save(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User update(User user);

    /**
     * 验证用户密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @param salt 盐值
     * @return 密码是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword, String salt);

    /**
     * 生成密码盐值
     *
     * @return 盐值
     */
    String generateSalt();

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword, String salt);
}
