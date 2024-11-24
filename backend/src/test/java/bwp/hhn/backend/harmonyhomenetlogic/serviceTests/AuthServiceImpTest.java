package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.AuthServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private MailService mailService;

    @Mock
    private UserDocumentConnectionRepository userDocumentConnectionRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private AuthServiceImp authService;

    private String accessToken;
    private User existingUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accessToken = "accessToken";

        existingUser = User.builder()
                .uuidID(UUID.randomUUID())
                .email("existing@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .firstName("Existing")
                .lastName("User")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("newpassword")
                .firstName("New")
                .lastName("User")
                .phoneNumber("123456789")
                .role(Role.ROLE_EMPLOYEE)
                .build();
    }

    @Test
    public void testRegister_Success() {
        // Given
        Jwt jwtToken = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "ROLE_ADMIN")
                .build();

        when(jwtDecoder.decode(accessToken)).thenReturn(jwtToken);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUuidID(UUID.randomUUID());
            return user;
        });
        List<Document> documents = Arrays.asList(
                Document.builder().uuidID(UUID.randomUUID()).documentName("Document1").documentType(DocumentType.OTHER).build(),
                Document.builder().uuidID(UUID.randomUUID()).documentName("Document2").documentType(DocumentType.OTHER).build()
        );
        when(documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED)).thenReturn(documents);

        // When
        String result = authService.register(registerRequest, accessToken);

        // Then
        assertEquals("You have successfully registered newuser@example.com", result);

        verify(jwtDecoder).decode(accessToken);
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(bCryptPasswordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(mailService).sendNotificationMail(
                eq("Welcome to Harmony Home Net"),
                eq("Your account has been successfully created."),
                eq(registerRequest.getEmail())
        );
        verify(userDocumentConnectionRepository, times(documents.size())).save(any(UserDocumentConnection.class));
    }

    @Test
    public void testRegister_EmailAlreadyExists() {
        // Given
        Jwt jwtToken = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "ROLE_ADMIN")
                .build();

        when(jwtDecoder.decode(accessToken)).thenReturn(jwtToken);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.register(registerRequest, accessToken));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email already exists", exception.getReason());

        verify(jwtDecoder).decode(accessToken);
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(mailService);
    }

    @Test
    public void testChangePassword_Success() {
        // Given
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";
        String email = "existing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        authService.changePassword(newPassword, confirmPassword, email);

        // Then
        verify(userRepository).findByEmail(email);
        verify(bCryptPasswordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
        verify(mailService).sendNotificationMail(
                eq("Password Change"),
                eq("Your password has been successfully changed."),
                eq(email)
        );
    }

    @Test
    public void testChangePassword_PasswordsDoNotMatch() {
        // Given
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";
        String email = "existing@example.com";

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.changePassword(newPassword, confirmPassword, email));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Passwords do not match", exception.getReason());

        verifyNoInteractions(userRepository);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(mailService);
    }

    @Test
    public void testChangePassword_UserNotFound() {
        // Given
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.changePassword(newPassword, confirmPassword, email));

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(mailService);
    }

    @Test
    public void testLogin_Success() throws UserNotFoundException {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("harmony-home-net")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .subject(existingUser.getEmail())
                .claim("userId", existingUser.getUuidID())
                .claim("role", existingUser.getRole().name())
                .build();

        Jwt jwt = Jwt.withTokenValue("accessToken")
                .headers(headers -> headers.put("alg", "none"))
                .claims(claims1 -> claims1.putAll(claims.getClaims()))
                .build();

        when(jwtEncoder.encode(any())).thenReturn(jwt);

        // When
        LoginResponse response = authService.login(authentication);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.accessToken());
        assertEquals(TokenType.Bearer, response.tokenType());

        verify(userRepository).findByEmail("existing@example.com");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    public void testLogin_UserNotFound() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.login(authentication));

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(jwtEncoder);
    }

    @Test
    public void testForgotPassword_Success() {
        // Given
        String email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedToken");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        authService.forgotPassword(email);

        // Then
        verify(userRepository).findByEmail(email);
        verify(bCryptPasswordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(mailService).sendNotificationMail(
                eq("Password Reset Request"),
                eq("encodedToken"),
                eq(email)
        );
    }

    @Test
    public void testForgotPassword_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.forgotPassword(email));

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(mailService);
    }

    @Test
    public void testResetPassword_Success() {
        // Given
        String token = "validToken";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        existingUser.setResetToken(token);
        existingUser.setResetTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(existingUser));
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        authService.resetPassword(token, newPassword, confirmPassword);

        // Then
        assertNull(existingUser.getResetToken());
        assertNull(existingUser.getResetTokenExpiry());
        verify(userRepository).findByResetToken(token);
        verify(bCryptPasswordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testResetPassword_InvalidToken() {
        // Given
        String token = "invalidToken";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        when(userRepository.findByResetToken(token)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.resetPassword(token, newPassword, confirmPassword));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid token", exception.getReason());

        verify(userRepository).findByResetToken(token);
        verifyNoInteractions(bCryptPasswordEncoder);
    }

    @Test
    public void testResetPassword_TokenExpired() {
        // Given
        String token = "expiredToken";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        existingUser.setResetToken(token);
        existingUser.setResetTokenExpiry(Instant.now().minus(1, ChronoUnit.HOURS));

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(existingUser));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.resetPassword(token, newPassword, confirmPassword));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Token has expired", exception.getReason());

        verify(userRepository).findByResetToken(token);
        verifyNoInteractions(bCryptPasswordEncoder);
    }

    @Test
    public void testResetPassword_PasswordsDoNotMatch() {
        // Given
        String token = "validToken";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        existingUser.setResetToken(token);
        existingUser.setResetTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(existingUser));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.resetPassword(token, newPassword, confirmPassword));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Passwords do not match", exception.getReason());

        verify(userRepository).findByResetToken(token);
        verifyNoInteractions(bCryptPasswordEncoder);
    }
}
