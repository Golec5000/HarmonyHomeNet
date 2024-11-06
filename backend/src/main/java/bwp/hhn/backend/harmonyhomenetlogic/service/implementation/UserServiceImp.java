package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.NotificationNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.NotificationTypeRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.UserService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final UserDocumentConnectionRepository userDocumentConnectionRepository;
    private final DocumentRepository documentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserResponse creatUser(UserRequest user) {
        User userEntity = User.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole() : Role.ROLE_OWNER)
                .build();

        User saved = userRepository.save(userEntity);

        List<Document> documents = documentRepository.findByDocumentTypeNot(DocumentType.PROPERTY_DEED);

        for (Document document : documents) {
            if (!userDocumentConnectionRepository.existsByDocumentUuidIDAndUserUuidID(document.getUuidID(), userEntity.getUuidID())) {
                UserDocumentConnection connection = UserDocumentConnection.builder()
                        .document(document)
                        .user(userEntity)
                        .build();

                userDocumentConnectionRepository.save(connection);

                if (userEntity.getUserDocumentConnections() == null)
                    userEntity.setUserDocumentConnections(new ArrayList<>());
                userEntity.getUserDocumentConnections().add(connection);

                if (document.getUserDocumentConnections() == null)
                    document.setUserDocumentConnections(new ArrayList<>());
                document.getUserDocumentConnections().add(connection);
            }
        }

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse updateUser(UUID userId, UserRequest user) throws UserNotFoundException {
        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        userEntity.setEmail(user.getEmail() != null ? user.getEmail() : userEntity.getEmail());
        userEntity.setFirstName(user.getFirstName() != null ? user.getFirstName() : userEntity.getFirstName());
        userEntity.setLastName(user.getLastName() != null ? user.getLastName() : userEntity.getLastName());
        userEntity.setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : userEntity.getPhoneNumber());

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
    public UserResponse assignRoleToUser(UUID userId, Role role) throws UserNotFoundException {
        User userEntity = userRepository.findByUuidIDOrEmail(userId, null)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        userEntity.setRole(role);

        User saved = userRepository.save(userEntity);

        return UserResponse.builder()
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .role(saved.getRole())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
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
                .build();
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);


        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getContent().stream()
                        .map(userEntity -> UserResponse.builder()
                                .userId(userEntity.getUuidID())
                                .email(userEntity.getEmail())
                                .firstName(userEntity.getFirstName())
                                .lastName(userEntity.getLastName())
                                .build()
                        )
                        .collect(Collectors.toList()),
                users.isLast()
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
                .build();
    }

    @Override
    public PageResponse<UserResponse> getUsersByRole(Role role, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAllByRole(role, pageable);

        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getContent().stream()
                        .map(userEntity -> UserResponse.builder()
                                .userId(userEntity.getUuidID())
                                .email(userEntity.getEmail())
                                .firstName(userEntity.getFirstName())
                                .lastName(userEntity.getLastName())
                                .build()
                        )
                        .collect(Collectors.toList()),
                users.isLast()
        );


    }

    @Override
    public String deleteUser(UUID userId) throws UserNotFoundException {

        if (!userRepository.existsByUuidID(userId))
            throw new UserNotFoundException("User id: " + userId + " not found");
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

        userEntity.getNotificationTypes().add(notificationType);
        notificationTypeRepository.save(notificationType);

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

}