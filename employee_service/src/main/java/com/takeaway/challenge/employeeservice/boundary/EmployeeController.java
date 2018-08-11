package com.takeaway.challenge.employeeservice.boundary;

import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.service.EmployeeService;
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

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/")
@Api(value="Employee-Service", description = "Manages employee entities ")
public class EmployeeController {
	private static final Logger logger = LogManager.getLogger(EmployeeController.class);

	private EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved employee's event list"),
			@ApiResponse(code = 404, message = "There are no events available for the specified employee UUID")
	})
	@ApiOperation(value = "Retrieve a list of events related to a specific employee in ascending creation order")
	@RequestMapping(method = RequestMethod.GET, value = "/employees/{uuid}", produces = {"application/json"})
	public String getEmployee(@PathVariable("uuid") String uuid) {
		try {
			Employee e = employeeService.getEmployee(uuid);
			return e.toString();

		} catch (Exception e) {
			return e.toString();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/employees")
	public ResponseEntity<Void> createEmployee() {
		try {
			Employee newEmp = new Employee()
					.setFullName("Reza Nirumand")
					.setHobbies(Arrays.asList("Guitar", "Piano"))
					.setEmail("reza@nirumand.com")
					.setBirthday(new Date());

			employeeService.createEmployee(newEmp);
			return new ResponseEntity<>(HttpStatus.CREATED);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/employees/{uuid}", consumes = {"application/json"})
	public ResponseEntity<Void> updateEmployee(@PathVariable("uuid") String uuid) {
		try {
			UUID id= UUID.fromString(uuid);
			Employee newEmp = new Employee()
					.setFullName("Reza Nirumand2")
					.setHobbies(Arrays.asList("Guitar", "Piano"))
					.setEmail("reza@nirumand.com")
					.setBirthday(new Date());

			employeeService.updateEmployee(id, newEmp);
			return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/employees/{uuid}", consumes = {"application/json"})
	public ResponseEntity<Void> deleteEmployee(@PathVariable("uuid") UUID uuid) {
		try {
			employeeService.deleteEmployee(uuid);
			return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
