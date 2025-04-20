package com.example.blog_system.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha验证码配置类
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer captchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 验证码宽度
        properties.setProperty("kaptcha.image.width", "150");
        // 验证码高度
        properties.setProperty("kaptcha.image.height", "50");
        // 验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // 验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 验证码字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 验证码字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        // 验证码干扰实现类
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        // 验证码干扰颜色
        properties.setProperty("kaptcha.noise.color", "blue");
        // 验证码文本字符渲染
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        // 验证码文本字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "2");
        // 验证码文本字符集合
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 验证码背景颜色渐变，开始颜色
        properties.setProperty("kaptcha.background.clear.from", "lightGray");
        // 验证码背景颜色渐变，结束颜色
        properties.setProperty("kaptcha.background.clear.to", "white");
        
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        
        return kaptcha;
    }
}
