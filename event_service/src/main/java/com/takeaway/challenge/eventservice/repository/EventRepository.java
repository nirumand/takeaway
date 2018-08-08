package com.takeaway.challenge.eventservice.repository;

import com.takeaway.challenge.eventservice.model.BusinessEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Responsible for persisting and fetching events
 */
@Repository
public interface EventRepository extends JpaRepository<BusinessEvent, UUID> {

	Optional<List<BusinessEvent>> findAllByEmployeeIdOrderByTimestampAsc(UUID uuid);
}
