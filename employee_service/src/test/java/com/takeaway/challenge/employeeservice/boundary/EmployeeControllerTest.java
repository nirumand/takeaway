package com.takeaway.challenge.employeeservice.boundary;

import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeNotFoundException;
import com.takeaway.challenge.employeeservice.boundary.exception.EmployeeServiceResponseEntityExceptionHandler;
import com.takeaway.challenge.employeeservice.model.Employee;
import com.takeaway.challenge.employeeservice.service.EmployeeService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

		Employee receivedEmployee = employeeController.parseRequestBody(result);
		assertThat(receivedEmployee).isEqualTo(emp);
	}

	@Test
	public void shouldReturnErrorMessage_whenGetEmployeeNotFound() throws Exception {
		UUID wrongId = UUID.randomUUID();
		when(employeeService.getEmployee(wrongId))
				.thenThrow(EmployeeNotFoundException.class);

		mockMvc.perform(get("/employees/{employeeId}", wrongId.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void shouldSucceed_createEmployee() throws Exception {
		String employeeString = "{\"email\":\"reza@nirumand.com\"," +
				"\"fullName\":\"Reza Nirumand\"," +
				"\"birthday\":\"1983-05-20\"," +
				"\"hobbies\":[\"Guitar\",\"Piano\"]}";

		mockMvc.perform(post("/employees/")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(employeeString)).andExpect(status().isCreated());

	}

	@Test
	public void shouldFail_createEmployeeNotParsableBody() throws Exception {
		String employeeString = "dummy-content";

		mockMvc.perform(post("/employees/")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(employeeString)).andExpect(status().isBadRequest());

	}

	@Test
	public void shouldSucceed_updateEmployee() throws Exception {
		// Prepare
		Employee emp = generateEmployee();
		when(employeeService.updateEmployee(emp.getEmployeeId(), emp))
				.thenReturn(emp);

		String updateString = "{\"email\":\"reza@nirumand.com\"," +
				"\"fullName\":\"Daniel Nirumand\"," +
				"\"birthday\":\"1983-06-20\"," +
				"\"hobbies\":[\"Guitar\",\"Piano\"]}";


		// Test
		String result = mockMvc.perform(put("/employees/{employeeId}", emp.getEmployeeId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateString))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		verify(employeeService, times(1)).updateEmployee(any(), any());
		JSONObject readJson = new JSONObject(result);

		assertThat(readJson).isNotNull();
		assertThat(readJson.getString("details")).isEqualTo(emp.getEmployeeId().toString());

	}

	@Test
	public void shouldFail_whenUpdateEmployeeNotFound() throws Exception {
		UUID wrongID = UUID.randomUUID();
		when(employeeService.updateEmployee(any(UUID.class), any(Employee.class)))
				.thenThrow(EmployeeNotFoundException.class);

		String updateString = "{\"email\":\"reza@nirumand.com\"," +
				"\"fullName\":\"Daniel Nirumand\"," +
				"\"birthday\":\"1983-06-20\"," +
				"\"hobbies\":[\"Guitar\",\"Piano\"]}";

		mockMvc.perform(put("/employees/{employeeId}", wrongID.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateString))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).updateEmployee(any(), any());
	}

	@Test
	public void shouldFail_whenUpdateEmployeeBadRequest() throws Exception {
		UUID id = UUID.randomUUID();
		String updateString = "dummy content";

		mockMvc.perform(put("/employees/{employeeId}", id.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateString)).andExpect(status().isBadRequest());
	}

	@Test
	public void shouldSucceed_whenDeleteEmployee() throws Exception {
		UUID id = UUID.randomUUID();

		mockMvc.perform(delete("/employees/{employeeId}", id.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldFail_whenDeleteEmployeeBadRequest() throws Exception {
		mockMvc.perform(delete("/employees/{employeeId}", "dummyId")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void shouldFail_whenDeleteEmployeeNotFound() throws Exception {
		doThrow(EmployeeNotFoundException.class).when(employeeService).deleteEmployee(any(UUID.class));

		mockMvc.perform(delete("/employees/{employeeId}", UUID.randomUUID().toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).deleteEmployee(any());
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