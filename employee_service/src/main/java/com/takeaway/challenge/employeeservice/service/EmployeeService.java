package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.boundary.EmployeeController;
import com.takeaway.challenge.employeeservice.event.BusinessEvent;
import com.takeaway.challenge.employeeservice.event.EventName;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.repository.EmployeeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
		return employeeRepository.findOne(id);
	}

	@Transactional
	public Employee createEmployee(Employee newEmp) {
		Employee emp = employeeRepository.save(newEmp);

		BusinessEvent event = new BusinessEvent();
		event.setEmployeeId(newEmp.getUuid())
				.setEventBody(newEmp.toString())
				.setEventName(EventName.EMPLOYEE_CREATED);

		if (!kafkaService.produce(event)) {
			logger.error("the producer failed to publish the event, hence rollback.");
			throw new RuntimeException("The event could not be published");
		}
		return emp;
	}

	@Transactional
	public void updateEmployee(UUID uuid, Employee emp) {
		if (employeeRepository.findOne(uuid) != null) {
			employeeRepository.save(emp);
			BusinessEvent event = new BusinessEvent();
			event.setEmployeeId(emp.getUuid())
					.setEventBody(emp.toString())
					.setEventName(EventName.EMPLOYEE_UPDATED);

			if (!kafkaService.produce(event)) {
				logger.error("the producer failed to publish the event, hence rollback.");
				throw new RuntimeException("The event could not be published");
			}
		}
	}

	@Transactional
	public void deleteEmployee(UUID uuid) {
		if (employeeRepository.findOne(uuid) != null) {
			employeeRepository.delete(uuid);
			BusinessEvent event = new BusinessEvent();
			event.setEmployeeId(uuid)
					.setEventName(EventName.EMPLOYEE_DELETED);
			if (!kafkaService.produce(event)) {
				logger.error("the producer failed to publish the event, hence rollback.");
				throw new RuntimeException("The event could not be published");
			}
		}
	}
}