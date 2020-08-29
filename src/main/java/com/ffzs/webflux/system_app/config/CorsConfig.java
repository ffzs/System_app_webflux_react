package com.ffzs.webflux.system_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author: ffzs
 * @Date: 2020/8/18 下午6:20
 */
@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许路径
                .allowedOrigins("*")  // 允许跨域访问的源
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")  // 允许请求地方法
                .maxAge(10000)  //预检间隔时间
                .allowedHeaders("*")  // 允许头部设置
                .allowCredentials(true);  // 是否发送cookie
    }
}

