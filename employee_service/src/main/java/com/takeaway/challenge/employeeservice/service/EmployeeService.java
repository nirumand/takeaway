package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeNotFoundException;
import com.takeaway.challenge.employeeservice.event.BusinessEvent;
import com.takeaway.challenge.employeeservice.event.EventName;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private KafkaService kafkaService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           KafkaService kafkaService) {
        this.employeeRepository = employeeRepository;
        this.kafkaService = kafkaService;
    }

    public Employee getEmployee(UUID id) {
        Employee emp = employeeRepository.findOne(id);
        if (emp == null) {
            throw new EmployeeNotFoundException(String.format("No employee found for UUID =[{}]", id));
        }
        return emp;
    }

    @Transactional
    public void createEmployee(Employee newEmp) {
        employeeRepository.save(newEmp);

        BusinessEvent event = new BusinessEvent();
        event.setEmployeeId(newEmp.getUuid())
                .setEventBody(newEmp.toString())
                .setEventName(EventName.EMPLOYEE_CREATED);

        kafkaService.produce(event);
    }

    public void updateEmployee(UUID uuid, Employee emp) {
        if (employeeRepository.findOne(uuid) != null) {
            employeeRepository.save(emp);
            BusinessEvent event = new BusinessEvent();
            event.setEmployeeId(emp.getUuid())
                    .setEventBody(emp.toString())
                    .setEventName(EventName.EMPLOYEE_UPDATED);

            kafkaService.produce(event);
        }
    }

    public void deleteEmployee(UUID uuid) {
        if (employeeRepository.findOne(uuid) != null) {
            employeeRepository.delete(uuid);
            BusinessEvent event = new BusinessEvent();
            event.setEmployeeId(uuid)
                    .setEventName(EventName.EMPLOYEE_UPDATED);
            kafkaService.produce(event);
        }
    }
}