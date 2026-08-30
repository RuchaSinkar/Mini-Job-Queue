package com.example.MiniJobQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniJobQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniJobQueueApplication.class, args);
	}

}
