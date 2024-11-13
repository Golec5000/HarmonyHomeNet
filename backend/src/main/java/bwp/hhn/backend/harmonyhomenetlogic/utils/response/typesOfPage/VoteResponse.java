package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import lombok.Builder;

import java.time.Instant;

@Builder
public record VoteResponse(
        Long id,
        VoteChoice voteChoice,
        Instant createdAt
) {
}
