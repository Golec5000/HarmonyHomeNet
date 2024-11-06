package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshToken;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshTokenRepository;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AuthService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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
    private final MailService mailService;

    @Override
    public LoginResponse register(RegisterRequest userRequest, HttpServletResponse response) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(userRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                .role(Role.ROLE_OWNER)
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();

        User saved = userRepository.save(user);

        String accessToken = createAccessToken(saved);
        String refreshToken = createRefreshToken(saved);

        saveRefreshToken(saved, refreshToken);
        createRefreshTokenCookie(response, refreshToken);

        mailService.sendNotificationMail(
                "Welcome to Harmony Home Net",
                "Your account has been successfully created.",
                saved.getEmail()
        );

        return new LoginResponse(accessToken, TokenType.Bearer);
    }

    @Override
    public LoginResponse login(Authentication authentication, HttpServletResponse response) throws UserNotFoundException {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        saveRefreshToken(user, refreshToken);
        createRefreshTokenCookie(response, refreshToken);

        return new LoginResponse(accessToken, TokenType.Bearer);
    }

    @Override
    public LoginResponse refreshToken(String authorizationHeader) throws UserNotFoundException {
        if (!authorizationHeader.startsWith(TokenType.Bearer.name())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        }

        String refreshToken = authorizationHeader.substring(7);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(token -> !token.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        User user = refreshTokenEntity.getUser();
        String newAccessToken = createAccessToken(user);

        return new LoginResponse(newAccessToken, TokenType.Bearer);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());
        user.setResetToken(token);
        user.setResetTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
        userRepository.save(user);

        mailService.sendNotificationMail(
                "Password Reset Request",
                token,
                user.getEmail()
        );
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (user.getResetTokenExpiry().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private String createAccessToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("userId", user.getUuidID())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createRefreshToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("scope", "SCOPE_REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();

        if (user.getRefreshTokens() == null) user.setRefreshTokens(new ArrayList<>());
        user.getRefreshTokens().add(refreshTokenEntity);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(60 * 60);
        response.addCookie(refreshTokenCookie);
    }
}