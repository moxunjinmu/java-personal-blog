package com.example.blog_system.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * Druid数据源配置类
 * 使用Spring Boot的自动配置机制
 */
@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
public class DruidConfig {
    // 使用默认的自动配置，无需额外配置
}
