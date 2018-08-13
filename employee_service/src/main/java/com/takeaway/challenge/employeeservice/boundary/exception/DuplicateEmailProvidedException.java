package com.takeaway.challenge.employeeservice.boundary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DuplicateEmailProvidedException extends RuntimeException {
	public DuplicateEmailProvidedException(String exception) {
		super(exception);
	}
}