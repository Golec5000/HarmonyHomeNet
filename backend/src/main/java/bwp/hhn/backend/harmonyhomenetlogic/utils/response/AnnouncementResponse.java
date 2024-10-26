package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnnouncementResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
