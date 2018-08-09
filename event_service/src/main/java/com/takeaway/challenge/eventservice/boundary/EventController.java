package com.takeaway.challenge.eventservice.boundary;

import com.takeaway.challenge.eventservice.boundary.exception.BadRequestException;
import com.takeaway.challenge.eventservice.boundary.exception.EventNotFoundException;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@Api(value="Event-Service", description = "Operates on events saved in the system")
public class EventController {

	private static final Logger logger = LogManager.getLogger(EventController.class);

	private EventService eventService;

	@Autowired
	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved employee's event list"),
			@ApiResponse(code = 404, message = "There are not events available for the specified employee UUID")
	})
	@ApiOperation(value = "Retrieve a list of events related to a specific employee in ascending creation order")
	@RequestMapping(method = RequestMethod.GET, value = "/events/{uuid}", produces = {"application/json"})
	public ResponseEntity<List<BusinessEvent>> getEmployee(@PathVariable("uuid") String uuid) {
		logger.debug("Request received with uuid:[{}]", uuid);
		try {
			UUID id = UUID.fromString(uuid);
			List<BusinessEvent> events = eventService.getBusinessEventsByEmployeeId(id);
			if (events == null) {
				throw new EventNotFoundException(String.format("No event found for employeeId: [%s]", uuid));
			}
			return new ResponseEntity<>(events, HttpStatus.OK);
		} catch (IllegalArgumentException il) {
			logger.debug("bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid UUID", uuid));
		}
	}
}