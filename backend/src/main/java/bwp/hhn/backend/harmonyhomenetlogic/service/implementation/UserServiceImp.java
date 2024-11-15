package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.NotificationNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.RSAKeyRecord;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.NotificationTypeRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.UserService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.NotificationTypeResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RSAKeyRecord rsaKeyRecord;

    @Override
    public UserResponse updateUser(UUID userId, UserRequest user, String accessToken) throws UserNotFoundException {

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.publicKey()).build();
        final Jwt jwtToken = jwtDecoder.decode(accessToken);

        String role = jwtToken.getClaim("role");

        // Check if the user is trying to update an admin
        if (Role.ROLE_ADMIN.equals(user.getRole()) && !Role.ROLE_ADMIN.equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can update other admins");
        }

        // Check if the user is an employee trying to update a non-owner
        if (Role.ROLE_EMPLOYEE.equals(role) && !Role.ROLE_OWNER.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employees can only update owners");
        }


        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        userEntity.setEmail(user.getEmail() != null ? user.getEmail() : userEntity.getEmail());
        userEntity.setFirstName(user.getFirstName() != null ? user.getFirstName() : userEntity.getFirstName());
        userEntity.setLastName(user.getLastName() != null ? user.getLastName() : userEntity.getLastName());
        userEntity.setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : userEntity.getPhoneNumber());
        userEntity.setRole(user.getRole() != null ? user.getRole() : userEntity.getRole());
        userEntity.setPassword(user.getPassword() != null ? bCryptPasswordEncoder.encode(user.getPassword()) : userEntity.getPassword());

        User saved = userRepository.save(userEntity);

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .updatedAt(saved.getUpdatedAt())
                .createdAt(saved.getCreatedAt())
                .phoneNumber(saved.getPhoneNumber())
                .build();
    }

    @Override
    public UserResponse getUserById(UUID userId) throws UserNotFoundException {
        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phoneNumber(userEntity.getPhoneNumber())
                .build();
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);


        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getContent().stream()
                        .map(userEntity -> UserResponse.builder()
                                .userId(userEntity.getUuidID())
                                .email(userEntity.getEmail())
                                .firstName(userEntity.getFirstName())
                                .lastName(userEntity.getLastName())
                                .role(userEntity.getRole())
                                .phoneNumber(userEntity.getPhoneNumber())
                                .createdAt(userEntity.getCreatedAt())
                                .updatedAt(userEntity.getUpdatedAt())
                                .build()
                        )
                        .collect(Collectors.toList()),
                users.isLast(),
                users.hasNext(),
                users.hasPrevious()
        );

    }

    @Override
    public UserResponse getUserByEmail(String email) throws UserNotFoundException {
        User userEntity = userRepository.findByUuidIDOrEmail(null, email)
                .orElseThrow(() -> new UserNotFoundException("User id: " + email + " not found"));

        return UserResponse.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phoneNumber(userEntity.getPhoneNumber())
                .build();
    }

    @Override
    public String deleteUser(UUID userId, String accessToken) throws UserNotFoundException {

        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.publicKey()).build();
        final Jwt jwtToken = jwtDecoder.decode(accessToken);

        String role = jwtToken.getClaim("role");

        // Check if the user is trying to delete an admin
        if (Role.ROLE_ADMIN.equals(userEntity.getRole()) && !Role.ROLE_ADMIN.equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can delete other admins");
        }

        // Check if the user is an employee trying to delete a non-owner
        if (Role.ROLE_EMPLOYEE.equals(role) && !Role.ROLE_OWNER.equals(userEntity.getRole())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employees can only delete owners");
        }

        userRepository.deleteById(userId);

        return "User deleted successfully";
    }

    @Override
    @Transactional
    public String addNotificationToUser(UUID userId, Notification notification) throws UserNotFoundException {
        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (userEntity.getNotificationTypes() == null) userEntity.setNotificationTypes(new ArrayList<>());

        // Check if the user has the required information for the notification type
        switch (notification) {
            case EMAIL:
                if (userEntity.getEmail() == null || userEntity.getEmail().isEmpty()) {
                    throw new IllegalArgumentException("User does not have an email address.");
                }
                break;
            case SMS:
                if (userEntity.getPhoneNumber() == null || userEntity.getPhoneNumber().isEmpty()) {
                    throw new IllegalArgumentException("User does not have a phone number.");
                }
                break;
            // Add other notification types and their required checks here
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + notification);
        }

        NotificationType notificationType = NotificationType.builder()
                .type(notification)
                .user(userEntity)
                .build();

        notificationTypeRepository.save(notificationType);
        userEntity.getNotificationTypes().add(notificationType);
        userRepository.save(userEntity);

        return "Notification " + notification + " added successfully";
    }

    @Override
    public String removeNotificationFromUser(UUID userId, Notification notification) throws UserNotFoundException, NotificationNotFoundException {

        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (userEntity.getNotificationTypes() == null) userEntity.setNotificationTypes(new ArrayList<>());

        boolean removed = userEntity.getNotificationTypes().removeIf(notificationType -> notificationType.getType().equals(notification));

        if (!removed) {
            throw new NotificationNotFoundException("Notification " + notification + " not found for user id: " + userId);
        }

        userRepository.save(userEntity);
        notificationTypeRepository.deleteByTypeAndUserUuidID(notification, userId);

        return "Notification " + notification + " removed successfully";
    }

    @Override
    public List<NotificationTypeResponse> getUserByNotifications(String email) throws UserNotFoundException {
        return notificationTypeRepository.findByUserEmail(email).stream()
                .map(
                        notificationType -> NotificationTypeResponse.builder()
                                .type(notificationType.getType())
                                .build()
                )
                .toList();
    }

}