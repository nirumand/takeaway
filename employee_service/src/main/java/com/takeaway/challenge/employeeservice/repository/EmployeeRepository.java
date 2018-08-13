package com.takeaway.challenge.employeeservice.repository;

import com.takeaway.challenge.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	Optional<Employee> findEmployeeByEmployeeId(UUID id);
}