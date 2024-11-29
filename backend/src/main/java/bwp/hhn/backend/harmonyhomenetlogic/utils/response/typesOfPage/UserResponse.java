package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt,
        Role role
) {
}
