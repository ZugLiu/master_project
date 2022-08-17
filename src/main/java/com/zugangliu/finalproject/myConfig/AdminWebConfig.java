package com.zugangliu.finalproject.myConfig;

import com.zugangliu.finalproject.interceptor.LoginInterceptor;
import com.zugangliu.finalproject.interceptor.VoteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/","/login","/signup","/kaptcha",
                        "/error", "/js/**", "/img/**", "/bootstrap/**",
                        "/svg/**", "/favicon.ico");
                        /*"/topics", "/getServerTime", "/topics/**", "/community", "/static/**"); */// these urls must be deleted when deploy
        /*registry.addInterceptor(new VoteInterceptor())
                .addPathPatterns("/option");*/
    }
}
