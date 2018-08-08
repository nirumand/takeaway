package com.takeaway.challenge.eventservice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.util.UUID;

/**
 * Entity to present all Bossiness events.
 */
@Entity
public class BusinessEvent {

	@Id
	@Column(name = "EVENT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID eventID;

	@Column(name = "EVENT_TIMESTAMP")
	private String timestamp;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVENT_NAME")
	private EventName eventName;

	@Column(name = "EMPLOYEE_ID")
	private UUID employeeId;

	@Column(name = "EVENT_BODY")
	private String eventBody;

	public BusinessEvent() {
	}

	public UUID getEventID() {
		return eventID;
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

	public BusinessEvent setEventID(UUID eventID) {
		this.eventID = eventID;
		return this;
	}

	public BusinessEvent setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
