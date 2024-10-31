package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DocumentResponse(
        UUID documentId,
        String documentName,
        DocumentType documentType,
        LocalDateTime createdAt,
        @JsonInclude(JsonInclude.Include.NON_NULL) byte[] documentDataBase64
) {
}