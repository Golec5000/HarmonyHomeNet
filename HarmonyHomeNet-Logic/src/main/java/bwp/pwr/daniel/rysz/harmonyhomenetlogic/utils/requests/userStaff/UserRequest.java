package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Setter
@Getter
public class UserRequest {

    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String PESELNumber;
    private String phoneNumber;
    private Set<String> role;
    private String gender;

}
