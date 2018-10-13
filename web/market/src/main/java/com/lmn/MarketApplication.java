package com.lmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketApplication.class, args);
	}
//	@Bean(name = "multipartResolver")
//	public CommonsMultipartResolver multipartResolver(){
//		return new CommonsMultipartResolver();
//	}
}
