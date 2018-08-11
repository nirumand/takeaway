package com.takeaway.challenge.employeeservice.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import com.takeaway.challenge.employeeservice.boundary.exception.BadRequestException;
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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

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
            @ApiResponse(code = 200, message = "Successfully retrieved employee's event list"),
            @ApiResponse(code = 404, message = "There is no employee available for the specified employee UUID")
    })
    @ApiOperation(value = "Retrieve an employee using the employeeId")
    @RequestMapping(method = RequestMethod.GET, value = "/employees/{uuid}", produces = {"application/json"})
    public String getEmployee(@PathVariable("uuid") String uuid) {
        try {
            UUID id = UUID.fromString(uuid);
            logger.debug("Received a request to GET employee details for UUID =[{}]", uuid);
            Employee emp = employeeService.getEmployee(id);
            return emp.toString();
        } catch (IllegalArgumentException il) {
            logger.debug("bad request received for uuid:[{}]", uuid, il);
            throw new BadRequestException(String.format("The input employeeId: [%s] is not a valid UUID", uuid));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/employees")
    public ResponseEntity<Void> createEmployee() {
        try {
            String emp = new Employee()
                    .setFullName("Reza Nirumand2")
                    .setHobbies(Arrays.asList("Guitar", "Piano"))
                    .setEmail("reza@nirumand.com")
                    .setBirthday(new Date()).toString();

            ObjectMapper mapper = new ObjectMapper();
            Employee newEmp = mapper.readValue(emp, Employee.class);

            employeeService.createEmployee(newEmp);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            logger.debug("Can not create employee object. Wrong data structure is specified");
            throw new BadRequestException(String.format("The input string can not be mmapeed" +
                    "to an employee object"));
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/employees/{uuid}", consumes = {"application/json"})
    public ResponseEntity<Void> updateEmployee(@PathVariable("uuid") String uuid,
                                               @RequestBody String body) {
        try {
            UUID id = UUID.fromString(uuid);
            String emp = new Employee()
                    .setFullName("Reza Nirumand2")
                    .setHobbies(Arrays.asList("Guitar", "Piano"))
                    .setEmail("reza@nirumand.com")
                    .setBirthday(new Date()).toString();

            ObjectMapper mapper = new ObjectMapper();
            Employee newEmp = mapper.readValue(emp, Employee.class);

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

    private Employee parseRequestBody(String employeeString){
        JSONObject readJson = new JSONObject(result);
        Employee emp= new Employee();

    }
}
