package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.BaseRole;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Gender;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        BaseRole baseRole,
        Gender gender
) {
}
