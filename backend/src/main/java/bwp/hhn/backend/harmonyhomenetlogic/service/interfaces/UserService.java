package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.NotificationNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.NotificationTypeResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse updateUser(UUID userId, UserRequest user, String accessToken) throws UserNotFoundException;

    UserResponse getUserById(UUID userId) throws UserNotFoundException;

    PageResponse<UserResponse> getAllUsers(int pageNo, int pageSize);

    UserResponse getUserByEmail(String email) throws UserNotFoundException;

    String deleteUser(UUID userId, String accessToken) throws UserNotFoundException;

    String addNotificationToUser(UUID userId, Notification notification) throws UserNotFoundException;

    String removeNotificationFromUser(UUID userId, Notification notification) throws UserNotFoundException, NotificationNotFoundException;

    List<NotificationTypeResponse> getUserByNotifications(String email) throws UserNotFoundException;
}
