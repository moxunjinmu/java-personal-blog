package com.example.blog_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 安全上下文仓库
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * 安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为我们使用的是API接口
            .csrf(csrf -> csrf.disable())
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 允许所有人访问登录、登出、验证码接口和静态资源
                .requestMatchers("/api/auth/login", "/api/auth/logout", "/api/captcha").permitAll()
                .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
