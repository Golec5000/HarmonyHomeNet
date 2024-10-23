package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.UserServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImp userService;

    private UUID userId;
    private User existingUser;

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
                .role(Role.USER)
                .build();
    }

    @Test
    public void testCreatUser() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse savedUser = userService.creatUser(userRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).isEqualTo("test@gmail.com");

        verify(userRepository).save(any(User.class));
    }


    @Test
    public void testUpdateUser_Success() throws UserNotFoundException {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .password("newpassword")
                .email("newemail@gmail.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse updatedUser = userService.updateUser(userId, userRequest);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.lastName()).isEqualTo("Nowak");
        assertThat(updatedUser.email()).isEqualTo("newemail@gmail.com");

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userRequest));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAssignRoleToUser_Success() throws UserNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse userResponse = userService.assignRoleToUser(userId, Role.ADMIN);

        assertThat(userResponse).isNotNull();
        assertThat(existingUser.getRole()).isEqualTo(Role.ADMIN);

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignRoleToUser(userId, Role.ADMIN));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserById_ValidUUID() throws UserNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserResponse userResponse = userService.getUserById(userId);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.email()).isEqualTo("test@gmail.com");

        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetUserById_InvalidUUID() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetAllUsers_WithUsers() {
        List<User> users = Arrays.asList(existingUser, User.builder()
                .uuidID(UUID.randomUUID())
                .firstName("Anna")
                .lastName("Nowak")
                .email("anna@gmail.com")
                .password("password2")
                .role(Role.USER)
                .build());

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> userResponses = userService.getAllUsers();

        assertThat(userResponses).hasSize(2);
        assertThat(userResponses.get(0).email()).isEqualTo("test@gmail.com");
        assertThat(userResponses.get(1).email()).isEqualTo("anna@gmail.com");

        verify(userRepository).findAll();
    }

    @Test
    public void testGetAllUsers_NoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> userResponses = userService.getAllUsers();

        assertThat(userResponses).isEmpty();

        verify(userRepository).findAll();
    }

    @Test
    public void testGetUserByEmail_UserExists() throws UserNotFoundException {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(existingUser));

        UserResponse userResponse = userService.getUserByEmail("test@gmail.com");

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.email()).isEqualTo("test@gmail.com");

        verify(userRepository).findByEmail("test@gmail.com");
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("nonexistent@gmail.com"));

        verify(userRepository).findByEmail("nonexistent@gmail.com");
    }

    @Test
    public void testGetUsersByRole_WithUsers() {
        List<User> users = Arrays.asList(existingUser, User.builder()
                .uuidID(UUID.randomUUID())
                .firstName("Anna")
                .lastName("Nowak")
                .email("anna@gmail.com")
                .password("password2")
                .role(Role.USER)
                .build());

        when(userRepository.findAllByRole(Role.USER)).thenReturn(users);

        List<UserResponse> userResponses = userService.getUsersByRole(Role.USER);

        assertThat(userResponses).hasSize(2);

        verify(userRepository).findAllByRole(Role.USER);
    }

    @Test
    public void testGetUsersByRole_NoUsers() {
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(Collections.emptyList());

        List<UserResponse> userResponses = userService.getUsersByRole(Role.ADMIN);

        assertThat(userResponses).isEmpty();

        verify(userRepository).findAllByRole(Role.ADMIN);
    }

    @Test
    public void testDeleteUser_Success() throws UserNotFoundException {
        when(userRepository.existsByUuidID(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        String result = userService.deleteUser(userId);

        assertThat(result).isEqualTo("User deleted successfully");

        verify(userRepository).existsByUuidID(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.existsByUuidID(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).existsByUuidID(userId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testCreatUser_WithNullRole() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(null)
                .build();

        User userWithDefaultRole = User.builder()
                .uuidID(UUID.randomUUID())
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithDefaultRole);

        UserResponse savedUser = userService.creatUser(userRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).isEqualTo("test@gmail.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    public void testCreatUser_WithSpecifiedRole() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(Role.EMPLOYEE)
                .build();

        User userWithSpecifiedRole = User.builder()
                .uuidID(UUID.randomUUID())
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(Role.EMPLOYEE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithSpecifiedRole);

        UserResponse savedUser = userService.creatUser(userRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).isEqualTo("test@gmail.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.EMPLOYEE);
    }

    @Test
    void shouldRemovePermissionFromUser() throws UserNotFoundException {
        UUID userId = UUID.randomUUID();
        AccessLevel accessLevel = AccessLevel.WRITE;

        User user = User.builder()
                .uuidID(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .accessLevel(AccessLevel.READ.getLevel() | AccessLevel.WRITE.getLevel())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.setAccessLevelRemovePermission(userId, accessLevel);

        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(user.getAccessLevel()).isEqualTo(AccessLevel.READ.getLevel());
    }

    @Test
    void shouldAddPermissionToUser() throws UserNotFoundException {
        UUID userId = UUID.randomUUID();
        AccessLevel accessLevel = AccessLevel.WRITE;

        User user = User.builder()
                .uuidID(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .accessLevel(AccessLevel.READ.getLevel())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.setAccessLevelAddPermission(userId, accessLevel);

        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(user.getAccessLevel()).isEqualTo(AccessLevel.READ.getLevel() | AccessLevel.WRITE.getLevel());
    }

}
