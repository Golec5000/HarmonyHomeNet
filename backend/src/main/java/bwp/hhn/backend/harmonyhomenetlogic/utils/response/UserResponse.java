package bwp.hhn.backend.harmonyhomenetlogic.utils.response;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Role role
) {
}
