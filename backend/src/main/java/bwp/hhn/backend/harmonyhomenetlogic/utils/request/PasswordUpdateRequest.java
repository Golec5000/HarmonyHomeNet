package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PasswordUpdateRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}