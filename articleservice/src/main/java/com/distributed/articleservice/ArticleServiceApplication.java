package com.distributed.articleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.distributed.articleservice.filter.JwtFilter;

@SpringBootApplication
public class ArticleServiceApplication {

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean bean = new FilterRegistrationBean<>();
		bean.setFilter(new JwtFilter());
		bean.addUrlPatterns("/api/*");
		return bean;
	}

	public static void main(String[] args) {
		SpringApplication.run(ArticleServiceApplication.class, args);
	}

}
