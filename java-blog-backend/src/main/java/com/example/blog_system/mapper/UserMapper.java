package com.example.blog_system.mapper;

import com.example.blog_system.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 影响的行数
     */
    int save(User user);
    
    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);
}
