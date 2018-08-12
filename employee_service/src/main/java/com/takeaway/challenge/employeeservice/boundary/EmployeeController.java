package com.takeaway.challenge.employeeservice.boundary;

import com.takeaway.challenge.employeeservice.boundary.exception.BadRequestException;
import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeNotFoundException;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@Api(value = "Employee-Service", description = "Manages employee entities ")
public class EmployeeController {
	private static final Logger logger = LogManager.getLogger(EmployeeController.class);

	private EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved employee's details."),
			@ApiResponse(code = 400, message = "Bad request is provided. The employeeId is not a valid employeeId."),
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
			logger.error("bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		}
	}

	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully created an Employee"),
			@ApiResponse(code = 400, message = "Bad request is provided, e.g., the request body could not be parsed to an employee.")
	})
	@ApiOperation(value = "Create an employee with the given details in request body.")
	@RequestMapping(method = RequestMethod.POST,
			value = "/employees",
			consumes = {"application/json"},
			produces = {"application/json"})
	public ResponseEntity<ResponseDetails> createEmployee(@RequestBody String requestBody) {
		try {
			Employee emp = employeeService.createEmployee(parseRequestBody(requestBody));
			ResponseDetails response = new ResponseDetails(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
					, "A mew Employee created."
					, emp.toString());

			return new ResponseEntity<>(response, HttpStatus.CREATED);

		} catch (JSONException | ParseException e) {
			logger.error("Can not create employee object. Wrong data structure is specified", e);
			throw new BadRequestException("The input string can not be mapped" +
					" to an employee object");
		}
	}
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully created an Employee"),
			@ApiResponse(code = 400, message = "Bad request is provided, e.g., the request body could not be parsed to an employee.")
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
					.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
					, "The Employee details are updated."
					, emp.toString());

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (IllegalArgumentException il) {
			logger.error("bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		} catch (JSONException | ParseException e) {
			logger.error("Can not create employee object. Wrong data structure is specified", e);
			throw new BadRequestException("The input string can not be mapped to an employee object");
		}
	}

	/** Deletes an Employee from persistent layer.
	 * @param uuid A string representing UUID format as employeeId
	 * @return ResponseDetails A message formatted as JSON if the resource is deleted.
	 */
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully deleted the Employee"),
			@ApiResponse(code = 400, message = "Bad request is provided, e.g., the employeeId is not valid.")
	})
	@ApiOperation(value = "Delete an employee based on the employeeId.")
	@RequestMapping(method = RequestMethod.DELETE, value = "/employees/{uuid}", consumes = {"application/json"})
	public ResponseEntity<ResponseDetails> deleteEmployee(@PathVariable("uuid") String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			employeeService.deleteEmployee(id);
			ResponseDetails response = new ResponseDetails(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
					, "The employee is deleted."
					, String.format("employeeId= [%s]", id));

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (IllegalArgumentException il) {
			logger.debug("bad request received for uuid:[{}]", uuid, il);
			throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid employeeId", uuid));
		}
	}

	/**
	 * Helper Method to parse string provided in http request body to an Employee Object
	 *
	 * @param employeeString : A json formatted string representing an Employee Object
	 * @return An Employee Object
	 * @throws JSONException,  When the text is not parsable to an Employee
	 * @throws ParseException, When the text is not parsable to an Employee
	 */
	private Employee parseRequestBody(String employeeString) throws JSONException, ParseException {
		JSONObject readJson = new JSONObject(employeeString);

		List<String> hobbies = readJson.getJSONArray("hobbies")
				.toList()
				.stream()
				.map(o -> (String) o)
				.collect(Collectors.toList());

		return new Employee(readJson.getString("email")
				, readJson.getString("fullName")
				, new SimpleDateFormat("yyyy-MM-dd").parse(readJson.getString("birthday"))
				, hobbies);
	}
}
