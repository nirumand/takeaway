package com.takeaway.challenge.employeeservice.boundary.exception;

public class ErrorDetails {
	private String errorTimestamp;
	private String message;
	private String errorDetails;

	public ErrorDetails(String errorTimestamp, String message, String errorDetails) {
		this.errorTimestamp = errorTimestamp;
		this.message = message;
		this.errorDetails = errorDetails;
	}

	public String getErrorTimestamp() {
		return errorTimestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorDetails() {
		return errorDetails;
	}
}
