package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.NotificationNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.RSAKeyRecord;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.UserServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.NotificationTypeResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationTypeRepository notificationTypeRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private RSAKeyRecord rsaKeyRecord;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private UserServiceImp userService;

    private static final RSAPublicKey publicKey;

    static {
        try {
            publicKey = (RSAPublicKey) KeyPairGenerator.getInstance("RSA").generateKeyPair().getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private UUID userId;
    private User existingUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        existingUser = User.builder()
                .uuidID(userId)
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(Role.ROLE_OWNER)
                .notificationTypes(new ArrayList<>())
                .build();

        accessToken = "accessToken";
        when(rsaKeyRecord.publicKey()).thenReturn(publicKey);
    }

    @Test
    public void testUpdateUser_Success() throws UserNotFoundException {
        // Given
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .password("newpassword")
                .email("newemail@gmail.com")
                .role(Role.ROLE_OWNER)
                .build();

        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));
        when(bCryptPasswordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Jwt jwtToken = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "ROLE_ADMIN")
                .claim("userId", existingUser.getUuidID())
                .build();

        when(jwtDecoder.decode(accessToken)).thenReturn(jwtToken);

        // When
        UserResponse updatedUser = userService.updateUser(userId, userRequest, accessToken);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.lastName()).isEqualTo("Nowak");
        assertThat(updatedUser.email()).isEqualTo("newemail@gmail.com");
        assertThat(existingUser.getPassword()).isEqualTo("encodedPassword");

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(bCryptPasswordEncoder).encode("newpassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UnauthorizedRoleChange() {
        // Given
        UserRequest userRequest = UserRequest.builder()
                .role(Role.ROLE_ADMIN)
                .build();

        existingUser.setRole(Role.ROLE_OWNER);
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));

        Jwt jwtToken = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "ROLE_EMPLOYEE")
                .claim("userId", existingUser.getUuidID())
                .build();

        when(jwtDecoder.decode(accessToken)).thenReturn(jwtToken);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.updateUser(userId, userRequest, accessToken));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Insufficient permissions to update or assign the role", exception.getReason());

        verify(userRepository, times(0)).findByUuidIDOrEmail(userId, null);
    }

    @Test
    public void testGetUserById_ValidUUID() throws UserNotFoundException {
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));

        UserResponse userResponse = userService.getUserById(userId);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.email()).isEqualTo("test@gmail.com");

        verify(userRepository).findByUuidIDOrEmail(userId, null);
    }

    @Test
    public void testGetUserById_InvalidUUID() {
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findByUuidIDOrEmail(userId, null);
    }

    @Test
    public void testGetAllUsers_Success() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<User> users = Collections.singletonList(existingUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        PageResponse<UserResponse> response = userService.getAllUsers(pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("test@gmail.com", response.content().get(0).email());

        verify(userRepository).findAll(pageable);
    }

    @Test
    public void testGetUserByEmail_UserExists() throws UserNotFoundException {
        when(userRepository.findByUuidIDOrEmail(null, "test@gmail.com")).thenReturn(Optional.of(existingUser));

        UserResponse userResponse = userService.getUserByEmail("test@gmail.com");

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.email()).isEqualTo("test@gmail.com");

        verify(userRepository).findByUuidIDOrEmail(null, "test@gmail.com");
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByUuidIDOrEmail(null, "nonexistent@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("nonexistent@gmail.com"));

        verify(userRepository).findByUuidIDOrEmail(null, "nonexistent@gmail.com");
    }

    @Test
    public void testDeleteUser_Success() throws UserNotFoundException {
        // Given
        existingUser.setPosts(new ArrayList<>());
        existingUser.setTopics(new ArrayList<>());

        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));

        Jwt jwtToken = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "ROLE_ADMIN")
                .claim("userId", existingUser.getUuidID())
                .build();

        when(jwtDecoder.decode(accessToken)).thenReturn(jwtToken);

        doNothing().when(userRepository).deleteById(userId);

        // When
        String result = userService.deleteUser(userId, accessToken);

        // Then
        assertEquals("User deleted successfully", result);

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(userRepository).deleteById(userId);
        verify(postRepository, times(existingUser.getPosts().size())).save(any(Post.class));
        verify(topicRepository, times(existingUser.getTopics().size())).save(any(Topic.class));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Given
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId, accessToken));

        verify(userRepository).findByUuidIDOrEmail(userId, null);
    }

    @Test
    public void testAddNotificationToUser_Success() throws UserNotFoundException {
        // Given
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));
        when(notificationTypeRepository.save(any(NotificationType.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = userService.addNotificationToUser(userId, Notification.EMAIL);

        // Then
        assertEquals("Notification EMAIL added successfully", result);
        assertEquals(1, existingUser.getNotificationTypes().size());
        assertEquals(Notification.EMAIL, existingUser.getNotificationTypes().get(0).getType());

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(notificationTypeRepository).save(any(NotificationType.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testAddNotificationToUser_NoEmail() throws UserNotFoundException {
        // Given
        existingUser.setEmail(null);
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addNotificationToUser(userId, Notification.EMAIL));
        assertEquals("User does not have an email address.", exception.getMessage());

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verifyNoInteractions(notificationTypeRepository);
    }

    @Test
    public void testRemoveNotificationFromUser_Success() throws UserNotFoundException, NotificationNotFoundException {
        // Given
        NotificationType notificationType = NotificationType.builder()
                .type(Notification.EMAIL)
                .user(existingUser)
                .build();
        existingUser.getNotificationTypes().add(notificationType);

        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));
        doNothing().when(notificationTypeRepository).deleteByTypeAndUserUuidID(Notification.EMAIL, userId);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = userService.removeNotificationFromUser(userId, Notification.EMAIL);

        // Then
        assertEquals("Notification EMAIL removed successfully", result);
        assertEquals(0, existingUser.getNotificationTypes().size());

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(notificationTypeRepository).deleteByTypeAndUserUuidID(Notification.EMAIL, userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRemoveNotificationFromUser_NotificationNotFound() throws UserNotFoundException {
        // Given
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(NotificationNotFoundException.class, () -> userService.removeNotificationFromUser(userId, Notification.EMAIL));

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verifyNoMoreInteractions(notificationTypeRepository);
    }

    @Test
    public void testGetUserByNotifications_Success() {
        // Given
        NotificationType notificationType = NotificationType.builder()
                .type(Notification.EMAIL)
                .user(existingUser)
                .build();
        List<NotificationType> notificationTypes = Collections.singletonList(notificationType);

        when(notificationTypeRepository.findByUserEmail("test@gmail.com")).thenReturn(notificationTypes);

        // When
        List<NotificationTypeResponse> responses = userService.getUserByNotifications("test@gmail.com");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(Notification.EMAIL, responses.get(0).type());

        verify(notificationTypeRepository).findByUserEmail("test@gmail.com");
    }

    @Test
    public void testGetUserByNotifications_UserNotFound() {
        // Given
        when(notificationTypeRepository.findByUserEmail("nonexistent@gmail.com")).thenReturn(Collections.emptyList());

        // When
        List<NotificationTypeResponse> responses = userService.getUserByNotifications("nonexistent@gmail.com");

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(notificationTypeRepository).findByUserEmail("nonexistent@gmail.com");
    }
}
