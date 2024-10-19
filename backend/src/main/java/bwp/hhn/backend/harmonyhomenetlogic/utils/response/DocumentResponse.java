package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentResponse(
        String documentName,
        String documentType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String documentDataBase64
) {
}
