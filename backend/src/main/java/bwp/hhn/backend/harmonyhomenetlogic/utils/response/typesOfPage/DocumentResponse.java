package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DocumentResponse(
        UUID documentId,
        String documentName,
        DocumentType documentType,
        Instant createdAt,
        @JsonInclude(JsonInclude.Include.NON_NULL) String documentExtension,
        @JsonInclude(JsonInclude.Include.NON_NULL) byte[] documentDataBase64
) {
}