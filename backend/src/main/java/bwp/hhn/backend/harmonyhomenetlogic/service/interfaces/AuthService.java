package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {

    String register(RegisterRequest registerRequest);

    LoginResponse login(Authentication authentication, HttpServletResponse response) throws UserNotFoundException;

    String logout(String email);

    LoginResponse refreshToken(String authorizationHeader) throws UserNotFoundException;

    String forgotPassword(String email);

    String generateAccessToken(UUID userId) throws UserNotFoundException;

}
