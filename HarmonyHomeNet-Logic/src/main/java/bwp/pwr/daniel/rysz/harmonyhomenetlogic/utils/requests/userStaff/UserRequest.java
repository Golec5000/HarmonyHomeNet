package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff;

import java.util.Set;

public record UserRequest(
        String password,
        String email,
        String firstName,
        String lastName,
        String PESELNumber,
        String phoneNumber,
        Set<String> role,
        String gender
) {}