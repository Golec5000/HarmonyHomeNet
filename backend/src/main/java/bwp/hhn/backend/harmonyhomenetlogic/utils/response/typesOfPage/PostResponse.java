package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PostResponse(
        UUID id,
        String content,
        Instant createdAt,
        String userName
) {
}
