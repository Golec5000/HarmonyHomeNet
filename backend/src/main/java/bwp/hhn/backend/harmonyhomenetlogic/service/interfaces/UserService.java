package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.NotificationNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse creatUser(UserRequest user);

    UserResponse updateUser(UUID userId, UserRequest user) throws UserNotFoundException;

    UserResponse assignRoleToUser(UUID userId, Role role) throws UserNotFoundException;

    UserResponse getUserById(UUID userId) throws UserNotFoundException;

    PageResponse<UserResponse> getAllUsers(int pageNo, int pageSize);

    UserResponse getUserByEmail(String email) throws UserNotFoundException;

    PageResponse<UserResponse> getUsersByRole(Role role, int pageNo, int pageSize);

    String deleteUser(UUID userId) throws UserNotFoundException;

    String addNotificationToUser(UUID userId, Notification notification) throws UserNotFoundException;

    String removeNotificationFromUser(UUID userId, Notification notification) throws UserNotFoundException, NotificationNotFoundException;
}
