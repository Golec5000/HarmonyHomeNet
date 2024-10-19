package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
