package com.takeaway.challenge.employeeservice.boundary;

import com.takeaway.challenge.employeeservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class EmployeeController {

	private EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService){
		this.employeeService=employeeService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/employees", consumes = {"application/json"})
	public ResponseEntity<EmployeeResource> getEmployee(String uuid) {
		try {
			return employeeService.getEmployee(uuid);

		} catch (Exception e) {
			unsubscriptionResponse = new UnsubscriptionResponse(unsubscriptionRequest.getRequestId());
			unsubscriptionResponse.addMsgHeader("Unidentified error.").addMsg(String.format("Please contact support with the following requestId:[%S].", unsubscriptionResponse.getRequestId()));
			return new ResponseEntity<>(unsubscriptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
