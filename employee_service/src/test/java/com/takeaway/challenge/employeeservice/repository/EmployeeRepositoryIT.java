package com.takeaway.challenge.employeeservice.repository;

import com.takeaway.challenge.employeeservice.model.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext
public class EmployeeRepositoryIT {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	public void shouldPersist_whenSaveEmployee() throws Exception {
		// Prepare
		Employee emp = generateEmployee();
		employeeRepository.save(emp);

		// Test
		Optional<Employee> found = employeeRepository.findEmployeeByEmployeeId(emp.getEmployeeId());
		assertThat(found).isPresent();
		assertThat(found.get()).isEqualTo(emp);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void shouldFail_persistDuplicateEmail() throws Exception {
		// Prepare
		Employee emp1 = generateEmployee();
		employeeRepository.save(emp1);

		// Test
		Employee emp2 = generateEmployee();
		employeeRepository.save(emp2);
		Optional<Employee> found = employeeRepository.findEmployeeByEmployeeId(emp2.getEmployeeId());
		assertThat(found).isNotPresent();
	}

	private Employee generateEmployee() throws Exception {
		Employee emp = new Employee();
		emp.setFullName("Reza Nirumand")
				.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1980-07-02"))
				.setEmail("Reza@Nirumand.com")
				.setHobbies(Arrays.asList("Piano", "Guitar"));

		return emp;
	}

}