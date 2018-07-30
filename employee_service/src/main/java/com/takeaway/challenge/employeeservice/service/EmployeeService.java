package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeService {

	private EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public Employee getEmployee(String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			return employeeRepository.findOne(id);
		} catch (IllegalArgumentException ie) {
			return null;
		}
	}
}
