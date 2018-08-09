package com.takeaway.challenge.eventservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Captures events published to the configured topic and persists it to the database.
 * If there is any problem with parsing the event or persisting, an error will be logged.
 */
@Service
public class KafkaService {
	private static final Logger logger = LogManager.getLogger(KafkaService.class);
	private ObjectMapper mapper;
	private EventRepository eventRepository;

	@Autowired
	public KafkaService(EventRepository eventRepository) {
		this.mapper = new ObjectMapper();
		this.eventRepository = eventRepository;
	}

	@KafkaListener(topics = "${spring.kafka.template.default-topic}")
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
