package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.UserService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.NotificationTypeResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //GET
    @GetMapping("/get-all-users")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageNo, pageSize));
    }

    @GetMapping("/get-user-by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/get-user-by-notifications")
    public ResponseEntity<List<NotificationTypeResponse>> getUserByNotifications(@RequestParam String email) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserByNotifications(email));
    }

    //PUT
    @PutMapping("/update-user-by-id")
    public ResponseEntity<UserResponse> updateUser(@RequestParam UUID userId, @RequestBody UserRequest userRequest, @RequestParam String accessToken) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUser(userId, userRequest, accessToken));
    }

    @PutMapping("/remove-notification-from-user")
    public ResponseEntity<String> removeNotificationFromUser(@RequestParam UUID userId, @RequestParam Notification notification) throws UserNotFoundException {
        return ResponseEntity.ok(userService.removeNotificationFromUser(userId, notification));
    }

    @PutMapping("/add-notification-to-user")
    public ResponseEntity<String> addNotificationToUser(@RequestParam UUID userId, @RequestParam Notification notification) throws UserNotFoundException {
        return ResponseEntity.ok(userService.addNotificationToUser(userId, notification));
    }

    //DELETE
    @DeleteMapping("/delete-user-by-id")
    public ResponseEntity<String> deleteUser(@RequestParam UUID userId, @RequestParam String accessToken) throws UserNotFoundException {
        return ResponseEntity.ok(userService.deleteUser(userId, accessToken));
    }
}