package com.example.blog_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 * 处理页面跳转请求
 */
@Controller
public class PageController {

    /**
     * 首页
     * 登录状态由前端判断
     *
     * @return 首页视图
     */
    @GetMapping("/")
    public String index() {
        // 直接返回首页，登录状态由前端判断
        return "index";
    }

    /**
     * 登录页
     *
     * @return 登录页视图
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
