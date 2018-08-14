package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.boundary.EmployeeController;
import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeNotFoundException;
import com.takeaway.challenge.employeeservice.event.BusinessEvent;
import com.takeaway.challenge.employeeservice.event.EventName;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.repository.EmployeeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
	private static final Logger logger = LogManager.getLogger(EmployeeController.class);


	private EmployeeRepository employeeRepository;
	private KafkaService kafkaService;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository,
						   KafkaService kafkaService) {
		this.employeeRepository = employeeRepository;
		this.kafkaService = kafkaService;
	}

	public Employee getEmployee(UUID id) {
		Optional<Employee> emp = employeeRepository.findEmployeeByEmployeeId(id);
		if (emp.isPresent()) {
			return emp.get();
		} else {
			throw new EmployeeNotFoundException(String.format("No Employee exists for employeeId=[%s]", id.toString()));
		}

	}

	@Transactional
	public Employee createEmployee(Employee newEmp) {
		Employee emp = employeeRepository.save(newEmp);

		BusinessEvent event = new BusinessEvent();
		event.setEmployeeId(newEmp.getEmployeeId())
				.setEventBody(newEmp.toString())
				.setEventName(EventName.EMPLOYEE_CREATED);

		if (!kafkaService.produce(event)) {
			logger.error("the producer failed to publish the event, hence rollback.");
			throw new RuntimeException("The event could not be published");
		}
		return emp;
	}

	@Transactional
	public Employee updateEmployee(UUID uuid, Employee emp) {
		Optional<Employee> found = employeeRepository.findEmployeeByEmployeeId(uuid);
		if (found.isPresent()) {
			Employee toUpdate = found.get();
			toUpdate.setHobbies(emp.getHobbies())
					.setBirthday(emp.getBirthday())
					.setEmail(emp.getEmail())
					.setFullName(emp.getFullName()); 
			
			employeeRepository.save(toUpdate);

			BusinessEvent event = new BusinessEvent();
			event.setEmployeeId(toUpdate.getEmployeeId())
					.setEventBody(found.get().toString())
					.setEventName(EventName.EMPLOYEE_UPDATED);

			if (!kafkaService.produce(event)) {
				String message = "the producer failed to publish the event, hence rollback.";
				logger.error(message);
				throw new RuntimeException(message);
			}
			return toUpdate;
		} else {
			String message = String.format("The employeeId= [%s] does not exist", uuid);
			logger.error(message);
			throw new EmployeeNotFoundException(message);
		}
	}

	@Transactional
	public void deleteEmployee(UUID uuid) {
		if (employeeRepository.findEmployeeByEmployeeId(uuid).isPresent()) {
			employeeRepository.delete(uuid);
			BusinessEvent event = new BusinessEvent();
			event.setEmployeeId(uuid)
					.setEventBody("N/A")
					.setEventName(EventName.EMPLOYEE_DELETED);
			if (!kafkaService.produce(event)) {
				String message = "the producer failed to publish the event, hence rollback.";
				logger.error(message);
				throw new RuntimeException(message);
			}
		} else {
			String message = String.format("The employeeId= [%s] does not exist", uuid);
			logger.error(message);
			throw new EmployeeNotFoundException(message);
		}

	}
}