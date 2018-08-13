package com.takeaway.challenge.employeeservice.boundary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.takeaway.challenge.employeeservice.boundary.exception.BadRequestException;
import com.takeaway.challenge.employeeservice.boundary.exception.DuplicateEmailProvidedException;
import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeNotFoundException;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/")
@Api(value = "Employee-Service", description = "Manages employee entities ")
public class EmployeeController {
	//TODO: enable Authentication

	private static final Logger logger = LogManager.getLogger(EmployeeController.class);

	private EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved employee's details."),
			@ApiResponse(code = 400, message = "Bad request is received. The employeeId is not a valid employeeId."),
			@ApiResponse(code = 404, message = "There is no employee available for the specified employeeId.")
	})
	@ApiOperation(value = "Retrieve an employee using the employeeId")
	@RequestMapping(method = RequestMethod.GET, value = "/employees/{uuid}", produces = {"application/json"})
	public String getEmployee(@PathVariable("uuid") String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			logger.debug("Received a request to GET employee details for UUID =[{}]", uuid);
			Employee emp = employeeService.getEmployee(id);
			if (emp == null) {
				throw new EmployeeNotFoundException(String.format("No employee found for UUID =[%s]", id));
			}
			return emp.toString();

		} catch (IllegalArgumentException il) {
			logger.error("Bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		}
	}

	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully created an Employee"),
			@ApiResponse(code = 400, message = "Bad request is received, e.g., the request body could not be parsed to an employee.")
	})
	@ApiOperation(value = "Create an employee with the given details in request body.")
	@RequestMapping(method = RequestMethod.POST,
			value = "/employees",
			consumes = {"application/json"},
			produces = {"application/json"})
	public ResponseEntity<ResponseDetails> createEmployee(@RequestBody String requestBody) {
		try {

			Employee emp = parseRequestBody(requestBody);
			employeeService.createEmployee(emp);
			ResponseDetails response = new ResponseDetails(
					ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
					"A new Employee created.",
					emp.toString());

			return new ResponseEntity<>(response, HttpStatus.CREATED);

		} catch (JsonSyntaxException e) {
			logger.error("Can not create employee object. Wrong data structure is specified", e);
			throw new BadRequestException("The input string can not be mapped" +
					" to an employee object");
		} catch (DataIntegrityViolationException d) {
			logger.debug("The email address already exists.");
			throw new DuplicateEmailProvidedException("The specified email already exists");
		}
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully created an Employee"),
			@ApiResponse(code = 400, message = "Bad request is received, e.g., the request body could not be parsed to an employee."),
			@ApiResponse(code = 403, message = "The email address already exists"),
			@ApiResponse(code = 404, message = "There is no employee available for the specified employeeId.")
	})
	@ApiOperation(value = "Update an employee's information for the given employeeId.")
	@RequestMapping(method = RequestMethod.PUT,
			value = "/employees/{uuid}",
			consumes = {"application/json"},
			produces = {"application/json"})
	public ResponseEntity<ResponseDetails> updateEmployee(@PathVariable("uuid") String uuid,
														  @RequestBody String requestBody) {
		try {
			UUID id = UUID.fromString(uuid);
			Employee emp = parseRequestBody(requestBody);
			employeeService.updateEmployee(id, emp);

			ResponseDetails response = new ResponseDetails(ZonedDateTime.now()
					.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
					"The Employee details are updated.",
					emp.toString());

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (IllegalArgumentException il) {
			logger.error("Bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		} catch (JsonSyntaxException e) {
			logger.error("Can not create employee object. Wrong data structure is specified", e);
			throw new BadRequestException("The input string can not be mapped to an employee object");
		}
	}

	/**
	 * Deletes an Employee from persistent laye
	 *
	 * @param uuid A string representing UUID format as employeeId
	 * @return ResponseDetails A message formatted as JSON if the resource is deleted.
	 */
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully deleted the Employee"),
			@ApiResponse(code = 400, message = "Bad request is received, e.g., the employeeId is not valid."),
			@ApiResponse(code = 404, message = "There is no employee available for the specified employeeId.")
	})
	@ApiOperation(value = "Delete an employee based on the employeeId.")
	@RequestMapping(method = RequestMethod.DELETE, value = "/employees/{uuid}", consumes = {"application/json"})
	public ResponseEntity<ResponseDetails> deleteEmployee(@PathVariable("uuid") String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			employeeService.deleteEmployee(id);
			ResponseDetails response = new ResponseDetails(
					ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
					"The employee is deleted.",
					String.format("employeeId= [%s]", id));

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (IllegalArgumentException il) {
			logger.debug("Bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		}
	}

	/**
	 * Helper Method to parse string (provided in http request body) to an Employee Object
	 *
	 * @param employeeString : A json formatted string representing an Employee Object
	 * @return An Employee Object
	 * @throws JsonSyntaxException, When the text is not parsable to an Employee
	 */
	Employee parseRequestBody(String employeeString) throws JsonSyntaxException {
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd").create();
		return gson.fromJson(employeeString, Employee.class);
	}
}
