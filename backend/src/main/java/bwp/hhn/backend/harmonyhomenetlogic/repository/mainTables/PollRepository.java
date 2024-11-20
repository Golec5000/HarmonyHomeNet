package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
    boolean existsByUuidID(UUID uuidID);

    List<Poll> findAllByEndDateBeforeAndNotificationSentFalse(Instant endDate);

    List<Poll> findAllByEndDateBefore(Instant now);
}