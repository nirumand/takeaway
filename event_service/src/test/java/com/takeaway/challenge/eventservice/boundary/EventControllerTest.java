package com.takeaway.challenge.eventservice.boundary;

import com.takeaway.challenge.eventservice.boundary.exception.EventServiceResponseEntityExceptionHandler;
import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.model.EventName;
import com.takeaway.challenge.eventservice.service.EventService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {

	private MockMvc mockMvc;

	@Mock
	private EventService eventService;

	@InjectMocks
	private EventController eventController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventController)
				.setControllerAdvice(new EventServiceResponseEntityExceptionHandler())
				.build();
	}

	@Test
	public void should_returnListOfBusinessEvents() throws Exception {
		// Preparing
		UUID eventId_1 = UUID.randomUUID();
		UUID eventId_2 = UUID.randomUUID();
		UUID employeeId = UUID.fromString("a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8");

		List<BusinessEvent> events = generateBusinessEvents(Arrays.asList(eventId_1, eventId_2));
		when(eventService.getBusinessEvents(employeeId)).thenReturn(events);

		// Testing
		String result = mockMvc.perform(get("/events/{employeeId}", employeeId.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JSONArray jsonObjects = new JSONArray(result);
		assertThat(jsonObjects.length()).isEqualTo(events.size());
		JSONObject object_1 = (JSONObject) jsonObjects.get(0);
		assertThat(object_1.get("eventID")).isEqualTo(events.get(0).getEventID().toString());
		assertThat(object_1.get("employeeId")).isEqualTo(events.get(0).getEmployeeId().toString());
		assertThat(object_1.get("eventName")).isEqualTo(events.get(0).getEventName().toString());
		assertThat(object_1.get("eventBody")).isEqualTo(events.get(0).getEventBody());

		JSONObject object_2 = (JSONObject) jsonObjects.get(1);
		assertThat(object_2.get("eventID")).isEqualTo(events.get(1).getEventID().toString());
		assertThat(object_2.get("employeeId")).isEqualTo(events.get(1).getEmployeeId().toString());
		assertThat(object_2.get("eventName")).isEqualTo(events.get(1).getEventName().toString());
		assertThat(object_2.get("eventBody")).isEqualTo(events.get(1).getEventBody());
	}

	@Test
	public void should_returnNotFoundMessage() throws Exception {
		// Random, Does not exist on system
		UUID employeeId = UUID.randomUUID();

		when(eventService.getBusinessEvents(employeeId)).thenReturn(null);

		String result = mockMvc.perform(get("/events/{employeeId}", employeeId.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JSONObject readJson = new JSONObject(result);

		assertThat(readJson).isNotNull();
		assertThat(readJson.getString("message"))
				.isEqualTo(String.format("No event found for employeeId: [%s]", employeeId));
		assertThat(readJson.getString("errorDetails"))
				.isEqualTo(String.format("uri=/events/%s", employeeId));
	}

	@Test
	public void should_returnBadRequestMessage() throws Exception {
		String dummyId = "non-valid-UUID-dummyId";
		String result = mockMvc.perform(get("/events/{employeeId}", dummyId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JSONObject readJson = new JSONObject(result);

		assertThat(readJson).isNotNull();
		assertThat(readJson.getString("message"))
				.isEqualTo(String.format("The input employeeId: [%s] is not a valid UUID", dummyId));
		assertThat(readJson.getString("errorDetails"))
				.isEqualTo(String.format("uri=/events/%s", dummyId));
	}

	/**
	 * Helper method for generating BusinessEvents for Testing
	 */
	private List<BusinessEvent> generateBusinessEvents(List<UUID> uuids) {
		BusinessEvent event1 = new BusinessEvent()
				.setEventBody("Employee{uuid=a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8," +
						" email='reza@nirumand.com', fullName='Reza Nirumand'," +
						" birthday='1983-08-09', hobbies=[Guitar, Piano]}")
				.setTimestamp("2018-08-09T10:22:44.118+02:00")
				.setEventName(EventName.EMPLOYEE_CREATED)
				.setEmployeeId(UUID.fromString("a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8"))
				.setEventID(uuids.get(0));

		BusinessEvent event2 = new BusinessEvent()
				.setEventBody("Employee{uuid=a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8," +
						" email='Daniel@nirumand.com', fullName='Daniel Nirumand'," +
						" birthday='1985-08-09', hobbies=[Biking]}")
				.setTimestamp("2018-08-09T10:22:44.118+02:00")
				.setEventName(EventName.EMPLOYEE_UPDATED)
				.setEmployeeId(UUID.fromString("a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8"))
				.setEventID(uuids.get(1));

		return Arrays.asList(event1, event2);
	}
}