package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String email;
}