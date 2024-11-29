package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsById(Long voteId);

    Page<Vote> findVotesByUserUuidID(UUID userId, Pageable pageable);

    boolean existsByPollUuidIDAndApartmentSignature(UUID pollId, String apartmentSignature);

    Page<Vote> findVotesByPollUuidID(UUID pollId, Pageable pageable);
}