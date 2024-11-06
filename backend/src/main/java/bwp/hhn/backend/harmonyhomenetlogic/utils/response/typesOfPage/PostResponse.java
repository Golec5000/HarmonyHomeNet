package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PostResponse(
        UUID id,
        String content,
        LocalDateTime createdAt,
        String userName
) {
}
