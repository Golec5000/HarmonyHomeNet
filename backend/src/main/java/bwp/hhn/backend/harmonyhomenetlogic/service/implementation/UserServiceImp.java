package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.NotificationTypeRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.UserService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    @Override
    public UserResponse creatUser(UserRequest user) {
        User userEntity = User.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole() : Role.USER)
                .build();

        userEntity.setAccessLevel(userEntity.getRole() == Role.ADMIN ? AccessLevel.READ.getLevel() | AccessLevel.WRITE.getLevel() | AccessLevel.DELETE.getLevel() :
                userEntity.getRole() == Role.EMPLOYEE ? AccessLevel.READ.getLevel() | AccessLevel.WRITE.getLevel() : AccessLevel.READ.getLevel()
        );

        User saved = userRepository.save(userEntity);

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse updateUser(UUID userId, UserRequest user) throws UserNotFoundException {
        User userEntity = getUserOrThrow(userId, null);

        userEntity.setEmail(user.getEmail() != null ? user.getEmail() : userEntity.getEmail());
        userEntity.setFirstName(user.getFirstName() != null ? user.getFirstName() : userEntity.getFirstName());
        userEntity.setLastName(user.getLastName() != null ? user.getLastName() : userEntity.getLastName());
        userEntity.setPassword(user.getPassword() != null ? user.getPassword() : userEntity.getPassword());

        User saved = userRepository.save(userEntity);

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    public UserResponse assignRoleToUser(UUID userId, Role role) throws UserNotFoundException {
        User userEntity = getUserOrThrow(userId, null);

        userEntity.setRole(role);

        User saved = userRepository.save(userEntity);

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .build();
    }

    @Override
    public UserResponse getUserById(UUID userId) throws UserNotFoundException {
        User userEntity = getUserOrThrow(userId, null);

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userEntity -> UserResponse.builder()
                        .email(userEntity.getEmail())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) throws UserNotFoundException {
        User userEntity = getUserOrThrow(null, email);

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role).stream()
                .map(userEntity -> UserResponse.builder()
                        .email(userEntity.getEmail())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String deleteUser(UUID userId) throws UserNotFoundException {

        if (!userRepository.existsByUuidID(userId))
            throw new UserNotFoundException("User id: " + userId + " not found");
        userRepository.deleteById(userId);

        return "User deleted successfully";
    }

    @Override
    public UserResponse setAccessLevelAddPermission(UUID userId, AccessLevel accessLevel) throws UserNotFoundException {

        User userEntity = getUserOrThrow(userId, null);
        userEntity.setAccessLevel(AccessLevel.addPermission(userEntity.getAccessLevel(), accessLevel));
        userRepository.save(userEntity);

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    @Override
    public UserResponse setAccessLevelRemovePermission(UUID userId, AccessLevel accessLevel) throws UserNotFoundException {

        User userEntity = getUserOrThrow(userId, null);
        userEntity.setAccessLevel(AccessLevel.removePermission(userEntity.getAccessLevel(), accessLevel));
        userRepository.save(userEntity);

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    @Override
    public String addNotificationToUser(UUID userId, Notification notification) throws UserNotFoundException {
        User userEntity = getUserOrThrow(userId, null);
        if (userEntity.getNotificationTypes() == null) userEntity.setNotificationTypes(new ArrayList<>());

        NotificationType notificationType = NotificationType.builder()
                .type(notification)
                .user(userEntity)
                .build();

        userEntity.getNotificationTypes().add(notificationType);
        notificationTypeRepository.save(notificationType);
        userRepository.save(userEntity);

        return "Notification " + notification + " added successfully";

    }

    @Override
    public String removeNotificationFromUser(UUID userId, Notification notification) throws UserNotFoundException {
        User userEntity = getUserOrThrow(userId, null);
        if (userEntity.getNotificationTypes() == null) userEntity.setNotificationTypes(new ArrayList<>());

        userEntity.getNotificationTypes().removeIf(notificationType -> notificationType.getType().equals(notification));

        userRepository.save(userEntity);
        notificationTypeRepository.deleteByTypeAndUserUuidID(notification, userId);

        return "Notification " + notification + " removed successfully";
    }

    private User getUserOrThrow(UUID userId, String email) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " or email: " + email + " not found")));
    }

}