package com.takeaway.challenge.employeeservice.boundary;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Messages returned to client
 */
public class ResponseDetails {

	@JsonProperty
	private String timestamp;

	@JsonProperty
	private String message;

	@JsonProperty
	private String details;

	public ResponseDetails(String timestamp, String message, String details) {
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

	@Override
	public String toString() {
		return "com.takeaway.challenge.employeeservice.boundary.ResponseDetails{" +
				"timestamp='" + timestamp + '\'' +
				", message='" + message + '\'' +
				", details='" + details + '\'' +
				'}';
	}
}
