package com.takeaway.challenge.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableAutoConfiguration
public class EventServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}
}
