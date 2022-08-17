package com.zugangliu.finalproject.myConfig;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;

//@Configuration
public class MyWebMVCConfig implements WebMvcConfigurer {
    // map static file directory path in .jar to the path in hard disc

   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
       // get directory of .jar
       ApplicationHome h = new ApplicationHome(getClass());
       File jarF = h.getSource();
       String filePath = jarF.getParentFile().toString()+"/upload/";
       System.out.println(filePath);

       registry.addResourceHandler("/static/img/upload/**").addResourceLocations("file:/"+filePath);
   }

}
