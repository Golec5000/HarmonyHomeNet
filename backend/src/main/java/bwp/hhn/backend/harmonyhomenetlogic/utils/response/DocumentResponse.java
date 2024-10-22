package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentResponse(
        String documentName,
        DocumentType documentType,
        LocalDateTime createdAt,
        String documentDataBase64
) {
}
