package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.event.BusinessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
	private KafkaTemplate<String, String> kafkaTemplate;
	private String kafkaTopic;

	@Autowired
	public KafkaService(KafkaTemplate<String, String> kafkaTemplate,
						@Value("${takeaway.kafka.topic}") String kafkaTopic) {

		this.kafkaTemplate = kafkaTemplate;
		this.kafkaTopic = kafkaTopic;
	}

	public void produce(BusinessEvent event) {
		kafkaTemplate.send(kafkaTopic, event.toString());
	}
}
