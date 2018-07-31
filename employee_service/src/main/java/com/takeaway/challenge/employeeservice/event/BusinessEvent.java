package com.takeaway.challenge.employeeservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BusinessEvent {

	private UUID eventID;
	private String timestamp;
	private EventName eventName;
	private UUID employeeId;
	private String eventBody;

	public BusinessEvent() {
		this.eventID = UUID.randomUUID();
		this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public EventName getEventName() {
		return eventName;
	}

	public BusinessEvent setEventName(EventName eventName) {
		this.eventName = eventName;
		return this;
	}

	public UUID getEmployeeId() {
		return employeeId;
	}

	public BusinessEvent setEmployeeId(UUID employeeId) {
		this.employeeId = employeeId;
		return this;
	}

	public String getEventBody() {
		return eventBody;
	}

	public BusinessEvent setEventBody(String eventBody) {
		this.eventBody = eventBody;
		return this;
	}

	@Override
	public String toString() {
		ObjectMapper mapper= new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
