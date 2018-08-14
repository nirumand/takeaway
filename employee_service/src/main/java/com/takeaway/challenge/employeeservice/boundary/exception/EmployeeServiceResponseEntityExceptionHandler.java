package com.takeaway.challenge.employeeservice.boundary.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class EmployeeServiceResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LogManager.getLogger(EmployeeServiceResponseEntityExceptionHandler.class);


	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
		logger.error("Unhandled exception occurred", ex);

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				"An internal error is occurred. Our team will work on this error",
				request.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EmployeeNotFoundException.class)
	public final ResponseEntity<ErrorDetails> handleEmployeeNotFoundException(
			EmployeeNotFoundException ex,
			WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateEmailProvidedException.class)
	public final ResponseEntity<ErrorDetails> handleDuplicateEmailProvidedException(
			DuplicateEmailProvidedException ex,
			WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);

	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<ErrorDetails> handleBadRequestException(
			BadRequestException ex,
			WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}