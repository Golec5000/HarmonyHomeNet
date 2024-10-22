package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record VoteResponse(
        VoteChoice voteChoice,
        LocalDateTime createdAt
) {
}
