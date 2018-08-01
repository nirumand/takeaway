package com.takeaway.challenge.eventservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaService {
	private ObjectMapper mapper;
	private EventRepository eventRepository;

	@Autowired
	public KafkaService(EventRepository eventRepository) {
		this.mapper = new ObjectMapper();
		this.eventRepository = eventRepository;
	}

	@KafkaListener(topics = "${takeaway.kafka.topic}")
	//@KafkaListener(topics = "codechallenge")
	public void onEventInCodeChallenge(String event) {
		try {
			BusinessEvent bEvent = mapper.readValue(event, BusinessEvent.class);
			eventRepository.save(bEvent);
			System.out.println(bEvent.toString());
		} catch (IOException io) {
			System.out.println(io.toString());
		}
	}
}
