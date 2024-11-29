package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AnnouncementResponse(
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
