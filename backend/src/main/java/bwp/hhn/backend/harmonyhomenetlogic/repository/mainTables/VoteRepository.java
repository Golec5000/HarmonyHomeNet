package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollUuidIDAndUserUuidIDAndApartmentSignature(UUID pollId, UUID userId, String apartmentSignature);
    boolean existsById(Long voteId);

    @Query("SELECT new bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse(v.id, v.voteChoice, v.createdAt) " +
            "FROM Vote v WHERE v.user.uuidID = :userId")
    List<VoteResponse> findVotesByUserId(UUID userId);
}