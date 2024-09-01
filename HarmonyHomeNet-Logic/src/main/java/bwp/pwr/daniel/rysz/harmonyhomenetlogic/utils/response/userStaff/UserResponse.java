package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Gender;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Set<Role> role,
        Gender gender
) {
}
