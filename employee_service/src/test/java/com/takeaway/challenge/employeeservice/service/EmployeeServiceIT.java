package com.takeaway.challenge.employeeservice.service;

import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.repository.EmployeeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext
public class EmployeeServiceIT {

	@Autowired
	private EntityManager em;


	@Mock
	private KafkaService kafkaService;

	@Autowired
	private EmployeeRepository employeeRepository;

	private EmployeeService employeeService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.employeeService = new EmployeeService(employeeRepository, kafkaService);
	}


	@Test
	public void shouldSucceed_createEmployee() throws Exception {
		// Prepare
		when(kafkaService.produce(any())).thenReturn(true);
		Employee emp1 = generateEmployee_1();
		Employee emp2 = generateEmployee_1();

		// Create Employee
		employeeService.createEmployee(emp1);
		assertThat(employeeService.getEmployee(emp1.getEmployeeId())).isEqualTo(emp1);
	}

	@Test
	public void shouldSucceed_updateEmployee() throws Exception {
		// Prepare
		when(kafkaService.produce(any())).thenReturn(true);
		Employee emp1 = generateEmployee_1();
		employeeService.createEmployee(emp1);
		assertThat(employeeService.getEmployee(emp1.getEmployeeId())).isEqualTo(emp1);
		em.flush();

		// Test
		emp1.setFullName("Daniel Nirumand");
		Employee updated = employeeService.updateEmployee(emp1.getEmployeeId(), generateEmployee_2());

		assertThat(updated).isEqualTo(emp1);

	}

	private Employee generateEmployee_1() throws Exception {
		Employee emp = new Employee();
		emp.setFullName("Reza Nirumand")
				.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1980-07-02"))
				.setEmail("Reza@Nirumand.com")
				.setHobbies(Arrays.asList("Piano", "Guitar"));

		return emp;
	}

	private Employee generateEmployee_2() throws Exception {
		Employee emp = new Employee();
		emp.setFullName("Reza Nirumand")
				.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1985-07-02"))
				.setEmail("Reza@Nirumand.com")
				.setHobbies(Arrays.asList("Piano", "Guitar"));

		return emp;
	}

}