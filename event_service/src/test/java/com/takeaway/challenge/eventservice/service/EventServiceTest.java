package com.takeaway.challenge.eventservice.service;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.model.EventName;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EventServiceTest {

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventService eventService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void shouldReturnEventList_whenGetBusinessEventsByEmployeeId() throws Exception {
		// Prepare
		UUID id = UUID.randomUUID();
		List<BusinessEvent> events = generateBusinessEvents();
		when(eventRepository.findAllByEmployeeIdOrderByTimestampAsc(id)).thenReturn(Optional.of(events));

		// Test
		List<BusinessEvent> foundEvents = eventService.getBusinessEventsByEmployeeId(id);
		assertThat(foundEvents.size()).isEqualTo(events.size());
		events.forEach(e -> assertThat(foundEvents.contains(events)));
	}

	@Test
	public void shouldReturnNull_whenGetBusinessEventsByEmployeeIdDoesNotExists() throws Exception {
		// Prepare
		UUID dummyId = UUID.randomUUID();
		List<BusinessEvent> events = generateBusinessEvents();
		when(eventRepository.findAllByEmployeeIdOrderByTimestampAsc(UUID.randomUUID())).thenReturn(Optional.of(events));

		// Test
		List<BusinessEvent> foundEvents = eventService.getBusinessEventsByEmployeeId(dummyId);
		assertThat(foundEvents).isNull();
	}

	/**
	 * Helper method for generating BusinessEvents for Testing
	 */
	private List<BusinessEvent> generateBusinessEvents() {
		UUID eventId_1 = UUID.randomUUID();
		UUID eventId_2 = UUID.randomUUID();
		BusinessEvent event1 = new BusinessEvent()
				.setEventBody("Employee{uuid=a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8," +
						" email='reza@nirumand.com', fullName='Reza Nirumand'," +
						" birthday='1983-08-09', hobbies=[Guitar, Piano]}")
				.setTimestamp("2018-08-09T10:22:44.118+02:00")
				.setEventName(EventName.EMPLOYEE_CREATED)
				.setEmployeeId(UUID.fromString("a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8"))
				.setEventID(eventId_1);

		BusinessEvent event2 = new BusinessEvent()
				.setEventBody("Employee{uuid=a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8," +
						" email='Daniel@nirumand.com', fullName='Daniel Nirumand'," +
						" birthday='1985-08-09', hobbies=[Biking]}")
				.setTimestamp("2018-08-09T10:22:44.118+02:00")
				.setEventName(EventName.EMPLOYEE_UPDATED)
				.setEmployeeId(UUID.fromString("a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8"))
				.setEventID(eventId_2);

		return Arrays.asList(event1, event2);
	}
}