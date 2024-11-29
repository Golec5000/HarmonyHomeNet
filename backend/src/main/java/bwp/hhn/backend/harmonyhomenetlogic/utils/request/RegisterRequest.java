package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotEmpty
    @Size(min = 3, max = 50)
    private String firstName;

    @NotEmpty
    @Size(min = 3, max = 50)
    private String lastName;

    @NotEmpty
    @Email
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @NotEmpty
    @Size(min = 10, max = 255)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(regexp = "^\\d{9,11}$", message = "Invalid phone number format")
    private String phoneNumber;

    private Role role;

}
