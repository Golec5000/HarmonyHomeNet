package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponse login(Authentication authentication, HttpServletResponse response) throws UserNotFoundException;

    LoginResponse refreshToken(String authorizationHeader) throws UserNotFoundException;

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    LoginResponse register(RegisterRequest userRequest, HttpServletResponse httpServletResponse);
}
