package com.takeaway.challenge.eventservice.service;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import com.takeaway.challenge.eventservice.model.EventName;
import com.takeaway.challenge.eventservice.repository.EventRepository;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@EnableKafka
@SpringBootTest
@DirtiesContext
@DataJpaTest
@EmbeddedKafka(partitions = 1, controlledShutdown = false,
		brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
public class KafkaServiceTest {


	private static String TOPIC = "codechallenge";

	@Value("127.0.0.1:9092")
	private String bootstrapServers;

//	@ClassRule
//	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(
//			1, false, 1, TOPIC);

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@Autowired
	private KafkaEmbedded kafkaEmbeded;

	@Autowired
	private EventRepository eventRepository;

	private KafkaService kafkaService;

	private KafkaTemplate<String, String> kafkaTemplate;

	@Before
	public void setUp() {
		Map<String, Object> props = new HashMap<>();
		// list of host:port pairs used for establishing the initial connections to the Kakfa cluster
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);

		ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);
		kafkaTemplate = new KafkaTemplate<>(producerFactory);


		kafkaService = new KafkaService(kafkaTemplate, TOPIC, eventRepository);
	}

	@Test
	public void should_consumeEventInCodeChallengeTopic() throws Exception {
		// 1) prepare
		BusinessEvent event = new BusinessEvent();
		UUID employeeID = UUID.randomUUID();
		UUID eventId = UUID.randomUUID();
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		EventName eventName = EventName.EMPLOYEE_DELETED;
		event.setEmployeeId(employeeID)
				.setEventName(eventName)
				.setEventBody("")
				.setEventID(eventId)
				.setTimestamp(timestamp);


		// 2) send an event
		kafkaService.produce(event);

		// 3) Testing: should have been persisted in repository
		Optional<List<BusinessEvent>> receieved = eventRepository.findAllByEmployeeIdOrderByTimestampAsc(employeeID);

		assertThat(receieved).isPresent();
		assertThat(receieved.get().size()).isEqualTo(1);

		BusinessEvent receivedEvent = receieved.get().get(0);

		assertThat(receivedEvent.getEventID()).isEqualByComparingTo(eventId);
		assertThat(receivedEvent.getTimestamp()).isEqualTo(timestamp);
		assertThat(receivedEvent.getEventBody()).isEqualTo("");
		assertThat(receivedEvent.getEventName()).isEqualByComparingTo(eventName);
	}
}