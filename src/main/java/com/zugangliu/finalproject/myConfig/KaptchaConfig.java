package com.zugangliu.finalproject.myConfig;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    /**
     * 配置生成图片的bean
     * @return
     */
    @Bean(name = "kaptchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //properties.setProperty("kaptcha.textproducer.char.string", "123456789");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
