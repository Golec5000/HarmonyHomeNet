package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollUuidIDAndUserUuidIDAndApartmentSignature(UUID pollId, UUID userId, String apartmentSignature);
    boolean existsById(Long voteId);
}