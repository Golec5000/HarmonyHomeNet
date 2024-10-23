package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import lombok.Builder;

@Builder
public record PossessionHistoryResponse(
        String userName,
        String apartmentName
) {
}
