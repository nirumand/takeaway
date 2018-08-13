package com.takeaway.challenge.employeeservice.boundary;

import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeServiceResponseEntityExceptionHandler;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.service.EmployeeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerTest {

	private MockMvc mockMvc;

	@Mock
	private EmployeeService employeeService;

	@InjectMocks
	private EmployeeController employeeController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
				.setControllerAdvice(new EmployeeServiceResponseEntityExceptionHandler())
				.build();
	}

	@Test
	public void shouldSucceed_getEmployee() throws Exception {
		// Preparing
		Employee emp = generateEmployee();
		UUID employeeId = emp.getEmployeeId();
		when(employeeService.getEmployee(employeeId)).thenReturn(emp);

		// Testing
		String result = mockMvc.perform(get("/employees/{employeeId}", employeeId.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Employee receivedEmployee= employeeController.parseRequestBody(result);
		assertThat(receivedEmployee).isEqualTo(emp);
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