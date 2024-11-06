package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TopicResponse(
        UUID id,
        String title,
        LocalDateTime createdAt,
        String userName
) {
}
