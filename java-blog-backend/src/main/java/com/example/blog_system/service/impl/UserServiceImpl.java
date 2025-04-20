package com.example.blog_system.service.impl;

import com.example.blog_system.mapper.UserMapper;
import com.example.blog_system.model.User;
import com.example.blog_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        // 如果盐值为空，生成新的盐值
        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            String salt = generateSalt();
            user.setSalt(salt);
            
            // 如果密码未加密，则进行加密
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                String encodedPassword = encodePassword(user.getPassword(), salt);
                user.setPassword(encodedPassword);
            }
        }
        
        // 保存用户
        try {
            userMapper.save(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("保存用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        // 如果密码不为空，则重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String salt = user.getSalt() != null ? user.getSalt() : generateSalt();
            user.setSalt(salt);
            String encodedPassword = encodePassword(user.getPassword(), salt);
            user.setPassword(encodedPassword);
        }
        
        // 更新用户
        userMapper.update(user);
        return user;
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword, String salt) {
        // 将原始密码和盐值组合
        String saltedPassword = rawPassword + salt;
        
        // 使用 BCryptPasswordEncoder 的 matches 方法来验证密码
        // 这会考虑到 BCrypt 算法中已包含的盐值
        boolean matches = passwordEncoder.matches(saltedPassword, encodedPassword);
        
        return matches;
    }

    @Override
    public String generateSalt() {
        // 生成随机UUID作为盐值
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String encodePassword(String rawPassword, String salt) {
        // 将原始密码和盐值组合，然后使用BCrypt算法加密
        String saltedPassword = rawPassword + salt;
        return passwordEncoder.encode(saltedPassword);
    }
}
