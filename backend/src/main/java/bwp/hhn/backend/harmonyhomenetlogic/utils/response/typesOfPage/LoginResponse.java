package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        TokenType tokenType
) {
}
