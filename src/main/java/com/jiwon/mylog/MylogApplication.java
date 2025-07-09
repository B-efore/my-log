package com.jiwon.mylog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class MylogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MylogApplication.class, args);
	}

}
