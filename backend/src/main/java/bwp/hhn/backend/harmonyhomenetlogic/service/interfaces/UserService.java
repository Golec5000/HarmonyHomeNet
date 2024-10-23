package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse creatUser(UserRequest user);

    UserResponse updateUser(UUID userId, UserRequest user) throws UserNotFoundException;

    UserResponse assignRoleToUser(UUID userId, Role role) throws UserNotFoundException;

    UserResponse getUserById(UUID userId) throws UserNotFoundException;

    List<UserResponse> getAllUsers();

    UserResponse getUserByEmail(String email) throws UserNotFoundException;

    List<UserResponse> getUsersByRole(Role role);

    String deleteUser(UUID userId) throws UserNotFoundException;

    UserResponse setAccessLevelAddPermission(UUID userId, AccessLevel accessLevel) throws UserNotFoundException;

    UserResponse setAccessLevelRemovePermission(UUID userId, AccessLevel accessLevel) throws UserNotFoundException;

    String addNotificationToUser(UUID userId, Notification notification) throws UserNotFoundException;

    String removeNotificationFromUser(UUID userId, Notification notification) throws UserNotFoundException;
}
