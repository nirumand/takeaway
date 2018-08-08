package com.takeaway.challenge.eventservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Captures events published to the configured topic and persists it to the database.
 * If there is any problem with parsing the event or persisting, an error will be logged.
 */
@Service
public class KafkaService {
	private static final Logger logger = LogManager.getLogger(KafkaService.class);
	private KafkaTemplate<String, String> kafkaTemplate;
	private String kafkaTopic;

	private ObjectMapper mapper;
	private EventRepository eventRepository;

	@Autowired
	public KafkaService(KafkaTemplate<String, String> kafkaTemplate,
						@Value("${takeaway.kafka.topic}") String kafkaTopic,
						EventRepository eventRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.mapper = new ObjectMapper();
		this.eventRepository = eventRepository;
		this.kafkaTopic = kafkaTopic;
	}

	public void produce(BusinessEvent event) {
		try {
			kafkaTemplate.send(kafkaTopic, event.toString()).get(3, TimeUnit.SECONDS);
			logger.info("event Sent: [%s]", event.toString());

		} catch (ExecutionException e) {
			logger.error("unable to send event= [%s]", event.toString(), e);

		} catch (TimeoutException | InterruptedException e) {
			logger.error("unable to send event= [%s]", event.toString(), e);
		}
	}

	//	@KafkaListener(topics = "${takeaway.kafka.topic}")
	@KafkaListener(topics = "codechallenge")
	public void onEventInCodeChallenge(String event) {
		try {
			logger.debug("Event received : [{}]", event);
			BusinessEvent bEvent = mapper.readValue(event, BusinessEvent.class);
			eventRepository.save(bEvent);
			logger.debug("Event Persisted : [{}]", bEvent.getEventID());
		} catch (IOException io) {
			logger.error("Event could not be persisted: [{}] with error: [{}]", event, io.getMessage());
		}
	}
}
