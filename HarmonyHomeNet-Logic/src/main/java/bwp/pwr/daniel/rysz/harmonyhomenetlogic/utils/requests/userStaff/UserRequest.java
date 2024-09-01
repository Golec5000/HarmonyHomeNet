package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
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