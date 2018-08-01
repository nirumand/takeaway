package com.takeaway.challenge.eventservice.boundary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@ControllerAdvice
public class EventServiceResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<ErrorDetails> handleBadRequestException(
			BadRequestException ex,
			WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(EventNotFoundException.class)
	public final ResponseEntity<ErrorDetails> handleUserNotFoundException(
			EventNotFoundException ex,
			WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
}