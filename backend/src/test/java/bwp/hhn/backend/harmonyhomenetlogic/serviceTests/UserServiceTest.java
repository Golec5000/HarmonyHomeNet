package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.UserServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
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

    @Mock
    private DocumentRepository documentRepository;

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
                .role(Role.ROLE_OWNER)
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

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUuidID(UUID.randomUUID()); // Simulate that the user has been saved with an ID
            return user;
        });

        when(documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED))
                .thenReturn(new ArrayList<>()); // Assuming no documents

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

        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse updatedUser = userService.updateUser(userId, userRequest);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.lastName()).isEqualTo("Nowak");
        assertThat(updatedUser.email()).isEqualTo("newemail@gmail.com");

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .build();

        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userRequest));

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAssignRoleToUser_Success() throws UserNotFoundException {
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponse userResponse = userService.assignRoleToUser(userId, Role.ROLE_ADMIN);

        assertThat(userResponse).isNotNull();
        assertThat(existingUser.getRole()).isEqualTo(Role.ROLE_ADMIN);

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        when(userRepository.findByUuidIDOrEmail(userId, null)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignRoleToUser(userId, Role.ROLE_ADMIN));

        verify(userRepository).findByUuidIDOrEmail(userId, null);
        verify(userRepository, never()).save(any(User.class));
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

//    @Test
//    public void testGetAllUsers_WithUsers() {
//        List<User> users = Arrays.asList(existingUser, User.builder()
//                .uuidID(UUID.randomUUID())
//                .firstName("Anna")
//                .lastName("Nowak")
//                .email("anna@gmail.com")
//                .password("password2")
//                .role(Role.ROLE_OWNER)
//                .build());
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        List<UserResponse> userResponses = userService.getAllUsers();
//
//        assertThat(userResponses).hasSize(2);
//        assertThat(userResponses.get(0).email()).isEqualTo("test@gmail.com");
//        assertThat(userResponses.get(1).email()).isEqualTo("anna@gmail.com");
//
//        verify(userRepository).findAll();
//    }

//    @Test
//    public void testGetAllUsers_NoUsers() {
//        when(userRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<UserResponse> userResponses = userService.getAllUsers(0,0);
//
//        assertThat(userResponses).isEmpty();
//
//        verify(userRepository).findAll();
//    }

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

//    @Test
//    public void testGetUsersByRole_WithUsers() {
//        List<User> users = Arrays.asList(existingUser, User.builder()
//                .uuidID(UUID.randomUUID())
//                .firstName("Anna")
//                .lastName("Nowak")
//                .email("anna@gmail.com")
//                .password("password2")
//                .role(Role.ROLE_OWNER)
//                .build());
//
//        when(userRepository.findAllByRole(Role.ROLE_OWNER)).thenReturn(users);
//
//        List<UserResponse> userResponses = userService.getUsersByRole(Role.ROLE_OWNER);
//
//        assertThat(userResponses).hasSize(2);
//
//        verify(userRepository).findAllByRole(Role.ROLE_OWNER);
//    }
//
//    @Test
//    public void testGetUsersByRole_NoUsers() {
//        when(userRepository.findAllByRole(Role.ROLE_ADMIN)).thenReturn(Collections.emptyList());
//
//        List<UserResponse> userResponses = userService.getUsersByRole(Role.ROLE_ADMIN);
//
//        assertThat(userResponses).isEmpty();
//
//        verify(userRepository).findAllByRole(Role.ROLE_ADMIN);
//    }

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

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUuidID(UUID.randomUUID()); // Simulate that the user has been saved with an ID
            return user;
        });

        when(documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED))
                .thenReturn(new ArrayList<>()); // Assuming no documents

        UserResponse savedUser = userService.creatUser(userRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).isEqualTo("test@gmail.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.ROLE_OWNER);
    }

    @Test
    public void testCreatUser_WithSpecifiedRole() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password1")
                .email("test@gmail.com")
                .role(Role.ROLE_EMPLOYEE)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUuidID(UUID.randomUUID()); // Simulate that the user has been saved with an ID
            return user;
        });

        when(documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED))
                .thenReturn(new ArrayList<>()); // Assuming no documents

        UserResponse savedUser = userService.creatUser(userRequest);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).isEqualTo("test@gmail.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.ROLE_EMPLOYEE);
    }

    // Additional test methods for methods involving other repositories can be added here,
    // mocking the respective repository methods used in the service implementation.

}
