package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.UserService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.UserRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;
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
    @GetMapping("/get-user/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get-user-by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/get-users-by-role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    //POST
    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.creatUser(userRequest));
    }

    //PUT
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID userId, @RequestBody UserRequest userRequest) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUser(userId, userRequest));
    }

    @PutMapping("/assign-role/{userId}")
    public ResponseEntity<UserResponse> assignRoleToUser(@PathVariable UUID userId, @RequestParam Role role) throws UserNotFoundException {
        return ResponseEntity.ok(userService.assignRoleToUser(userId, role));
    }

    @PutMapping("/remove-notification/{userId}")
    public ResponseEntity<String> removeNotificationFromUser(@PathVariable UUID userId, @RequestParam Notification notification) throws UserNotFoundException {
        return ResponseEntity.ok(userService.removeNotificationFromUser(userId, notification));
    }

    @PutMapping("/add-notification/{userId}")
    public ResponseEntity<String> addNotificationToUser(@PathVariable UUID userId, @RequestParam Notification notification) throws UserNotFoundException {
        return ResponseEntity.ok(userService.addNotificationToUser(userId, notification));
    }

    //DELETE
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}