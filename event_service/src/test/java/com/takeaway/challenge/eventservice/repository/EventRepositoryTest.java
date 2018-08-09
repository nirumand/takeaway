package com.takeaway.challenge.eventservice.repository;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.model.EventName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EventRepositoryTest {

	@Autowired
	private EventRepository eventRepository;

	@Test
	public void shouldReturnEvents_whenFindAllByEmployeeIdOrderByTimestampAsc() throws Exception {
		List<BusinessEvent> businessEvents = generateBusinessEvents();

		// Check if it is empty at first
		assertThat(eventRepository.findAll().size()).isEqualTo(0);

		// Save some events for testing
		eventRepository.save(businessEvents);

		// Check if they are saved
		for (BusinessEvent event : businessEvents) {
			assertThat(eventRepository.findAllByEmployeeIdOrderByTimestampAsc(event.getEmployeeId())).isPresent();
		}
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