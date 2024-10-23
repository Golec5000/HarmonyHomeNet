package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PollResponse(
        String pollName,
        String content,
        byte[] uploadData,
        LocalDateTime createdAt,
        LocalDateTime endDate,
        BigDecimal summary
) {
}