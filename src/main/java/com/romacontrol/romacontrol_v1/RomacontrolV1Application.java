package com.romacontrol.romacontrol_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
@EnableScheduling   
public class RomacontrolV1Application {	

	public static void main(String[] args) {
		SpringApplication.run(RomacontrolV1Application.class, args);
	}

}
					