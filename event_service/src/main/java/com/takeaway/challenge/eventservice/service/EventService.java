package com.takeaway.challenge.eventservice.service;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides the required information requested by {@link com.takeaway.challenge.eventservice.boundary.EventController}
 */
@Service
public class EventService {
	private static final Logger logger = LogManager.getLogger(EventService.class);

	private EventRepository eventRepository;

	@Autowired
	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public List<BusinessEvent> getBusinessEventsByEmployeeId(UUID uuid) {
		logger.debug("Processing request for uuid: [{}]", uuid);
		Optional<List<BusinessEvent>> events = eventRepository.findAllByEmployeeIdOrderByTimestampAsc(uuid);
		if (events.isPresent()) {
			return new ArrayList<>(events.get());
		}
		logger.debug("No event found for uuid:[{}]", uuid);
		return null;
	}
}