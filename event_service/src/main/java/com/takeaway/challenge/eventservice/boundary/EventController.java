package com.takeaway.challenge.eventservice.boundary;

import com.takeaway.challenge.eventservice.boundary.exception.BadRequestException;
import com.takeaway.challenge.eventservice.boundary.exception.EventNotFoundException;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class EventController {

	private EventService eventService;

	@Autowired
	public EventController(EventService employeeService) {
		this.eventService = employeeService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/events/{uuid}", produces = {"application/json"})
	public ResponseEntity<List<BusinessEvent>> getEmployee(@PathVariable("uuid") String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			List<BusinessEvent> events = eventService.getBusinessEvents(id);
			if (events == null) {
				throw new EventNotFoundException(String.format("No event found for employeeId: [%s]", uuid));
			}
			return new ResponseEntity<>(events, HttpStatus.OK);
		} catch (IllegalArgumentException il) {
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid UUID", uuid));
		}
	}
}