package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String login;
    private String password;
}
