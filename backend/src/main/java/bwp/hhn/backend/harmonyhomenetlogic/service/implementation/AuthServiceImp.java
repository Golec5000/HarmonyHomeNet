package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshToken;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshTokenRepository;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AuthService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String register(RegisterRequest registerRequest) {

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        userRepository.save(user);

        return "Pomyślana rejestracja";
    }

    @Override
    public LoginResponse login(Authentication authentication, HttpServletResponse response) throws UserNotFoundException {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("userId", user.getUuidID())
                .claim("role", user.getRole().name())
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", "SCOPE_REFRESH_TOKEN")
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        System.out.println("Refresh token: " + refreshToken);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();

        if(user.getRefreshTokens() == null) user.setRefreshTokens(new ArrayList<>());
        user.getRefreshTokens().add(refreshTokenEntity);

        refreshTokenRepository.save(refreshTokenEntity);

        creatRefreshTokenCookie(response,refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType(TokenType.Bearer)
                .build();
    }

    @Override
    public LoginResponse refreshToken(String authorizationHeader) throws UserNotFoundException {

        if (!authorizationHeader.startsWith(TokenType.Bearer.name())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        // Find refreshToken from database and ensure it is not revoked
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(token -> !token.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        User user = refreshTokenEntity.getUser();

        // Use the authentication object to generate a new accessToken
        JwtClaimsSet accessToken = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("userId", user.getUuidID())
                .claim("role", user.getRole().name())
                .build();

        String newAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .tokenType(TokenType.Bearer)
                .build();
    }

    @Override
    public String logout(String email) {
        return "";
    }

    @Override
    public String forgotPassword(String email) {
        return "";
    }

    @Override
    public String generateAccessToken(UUID userId) throws UserNotFoundException {
        return "";
    }

    private Cookie creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(60 * 60);
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }
}
