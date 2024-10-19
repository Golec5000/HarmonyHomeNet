package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

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

    @NotEmpty
    @Pattern(regexp = "^\\d{9,11}$", message = "Invalid phone number format")
    private String phoneNumber;

    private Role role;

}