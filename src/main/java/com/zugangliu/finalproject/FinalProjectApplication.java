package com.zugangliu.finalproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.FormContentFilter;

@SpringBootApplication
@MapperScan("com.zugangliu.finalproject.mapper")
@Import(FormContentFilter.class)
public class FinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

}
