package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record TopicResponse(
        UUID id,
        String title,
        Instant createdAt,
        String userName
) {
}
