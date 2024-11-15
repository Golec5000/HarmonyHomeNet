package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponse login(Authentication authentication) throws UserNotFoundException;

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    String register(RegisterRequest userRequest, String accessToken);

    void changePassword(String newPassword, String confirmPassword, String email);
}
