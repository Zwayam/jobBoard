package com.zwayam.jobboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.zwayam")
@SpringBootApplication
public class JobBoardApp {

	public static void main(String[] args) {
		SpringApplication.run(JobBoardApp.class, args);
		System.out.println("Application Started");
	}

}
