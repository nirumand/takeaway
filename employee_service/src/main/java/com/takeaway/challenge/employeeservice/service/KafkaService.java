package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.event.BusinessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaService {
	private static final Logger logger = LogManager.getLogger(KafkaService.class);
	private KafkaTemplate<String, String> kafkaTemplate;
	private String kafkaTopic;

	@Autowired
	public KafkaService(KafkaTemplate<String, String> kafkaTemplate,
						@Value("${takeaway.kafka.topic}") String kafkaTopic) {

		this.kafkaTemplate = kafkaTemplate;
		this.kafkaTopic = kafkaTopic;
	}

	public void produce(BusinessEvent event) {
		try {
			kafkaTemplate.send(kafkaTopic, event.toString()).get(100, TimeUnit.MILLISECONDS);
			logger.info("event Sent: [%s]", event.toString());

		} catch (ExecutionException e) {
			logger.error("unable to send event= [%s]", event.toString(), e);

		} catch (TimeoutException | InterruptedException e) {
			logger.error("unable to send event= [%s]", event.toString(), e);
		}
	}
}
