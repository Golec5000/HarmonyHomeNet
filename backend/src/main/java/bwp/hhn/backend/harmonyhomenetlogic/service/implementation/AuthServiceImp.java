package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AuthService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;
    private final MailService mailService;
    private final UserDocumentConnectionRepository userDocumentConnectionRepository;
    private final DocumentRepository documentRepository;
    private final JwtDecoder jwtDecoder;

    @Override
    @Transactional
    public String register(RegisterRequest userRequest, String accessToken) {

        final Jwt jwtToken = jwtDecoder.decode(accessToken);

        Role role = Role.valueOf(jwtToken.getClaim("role"));

        if (role.getLevel() < userRequest.getRole().getLevel()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Insufficient permissions to update or assign the role");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(userRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();

        User saved = userRepository.save(user);

        List<Document> documents = documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED);

        for (Document document : documents) {
            if (!userDocumentConnectionRepository.existsByDocumentUuidIDAndUserUuidID(document.getUuidID(), user.getUuidID())) {
                UserDocumentConnection connection = UserDocumentConnection.builder()
                        .document(document)
                        .user(user)
                        .build();

                userDocumentConnectionRepository.save(connection);

                if (user.getUserDocumentConnections() == null)
                    user.setUserDocumentConnections(new ArrayList<>());
                user.getUserDocumentConnections().add(connection);

                if (document.getUserDocumentConnections() == null)
                    document.setUserDocumentConnections(new ArrayList<>());
                document.getUserDocumentConnections().add(connection);
            }
        }

        mailService.sendNotificationMail(
                "Welcome to Harmony Home Net",
                "Your account has been successfully created.",
                saved.getEmail()
        );

        return "You have successfully registered " + saved.getEmail();
    }

    @Override
    public void changePassword(String newPassword, String confirmPassword, String email) {

        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));

        userRepository.save(user);

        mailService.sendNotificationMail(
                "Password Change",
                "Your password has been successfully changed.",
                user.getEmail()
        );
    }

    @Override
    public LoginResponse login(Authentication authentication) throws UserNotFoundException {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = createAccessToken(user);

        return new LoginResponse(accessToken, TokenType.Bearer);
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
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

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
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("userId", user.getUuidID())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}