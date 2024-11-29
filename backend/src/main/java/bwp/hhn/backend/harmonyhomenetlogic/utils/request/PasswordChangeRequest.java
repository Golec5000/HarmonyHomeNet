package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangeRequest {
    private String newPassword;
    private String confirmPassword;
    private String email;
}
