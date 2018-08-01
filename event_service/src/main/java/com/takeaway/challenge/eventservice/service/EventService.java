package com.takeaway.challenge.eventservice.service;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

	private EventRepository eventRepository;
	private KafkaService kafkaService;

	@Autowired
	public EventService(EventRepository employeeRepository,
						KafkaService kafkaService) {
		this.eventRepository = employeeRepository;
		this.kafkaService = kafkaService;
	}

	public List<BusinessEvent> getBusinessEvents(UUID uuid) {
		Optional<List<BusinessEvent>> events = eventRepository.findAllByEmployeeIdOrderByTimestampAsc(uuid);
		if (events.isPresent()) {
			return events.get().stream().collect(Collectors.toList());
		}
		return null;
	}
}