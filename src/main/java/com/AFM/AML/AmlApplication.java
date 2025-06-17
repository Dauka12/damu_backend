package com.AFM.AML;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmlApplication {
	@Value("${spring.port}") private static String sender;
	public static void main(String[] args) {
		SpringApplication.run(AmlApplication.class, args);
//		System.out.println("Port is " + sender);
	}

}
