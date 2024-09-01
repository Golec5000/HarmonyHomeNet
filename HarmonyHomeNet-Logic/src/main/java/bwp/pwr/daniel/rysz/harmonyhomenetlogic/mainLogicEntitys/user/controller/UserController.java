package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Employee;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service.UserService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.BaseRole;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.UserRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.mapUserListToUserResponseList(userService.findAll()));
    }

    @GetMapping("/user-by-id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.mapUserToUserResponse(userService.findById(UUID.fromString(userId))));
    }

    @GetMapping("/user-by-login/{userLogin}")
    public ResponseEntity<UserResponse> getUserByLogin(@PathVariable String userLogin) {
        return ResponseEntity.ok(userService.mapUserToUserResponse(userService.findByLogin(userLogin)));
    }

    @GetMapping("/user-by-email/{userEmail}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String userEmail) {
        return ResponseEntity.ok(userService.mapUserToUserResponse(userService.findByEmail(userEmail)));
    }

    @GetMapping("/user-by-PESELNumber/{userPESELNumber}")
    public ResponseEntity<UserResponse> getUserByPESELNumber(@PathVariable String userPESELNumber) {
        return ResponseEntity.ok(userService.mapUserToUserResponse(userService.findByPESELNumber(userPESELNumber)));
    }

    @PostMapping("/add-resident")
    public ResponseEntity<UserResponse> addResident(@RequestBody UserRequest newUser) {
        Resident resident = (Resident) createUser(newUser);
        resident.setBaseRole(BaseRole.USER);
        return ResponseEntity.ok(userService.save(resident));
    }

    @PostMapping("/add-employee")
    public ResponseEntity<UserResponse> addEmployee(@RequestBody UserRequest newUser) {
        Employee employee = (Employee) createUser(newUser);
        employee.setBaseRole(BaseRole.EMPLOYEE);
        return ResponseEntity.ok(userService.save(employee));
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<User> deleteUserById(@PathVariable String userId) {
        userService.deleteById(UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }

    private User createUser(@NonNull UserRequest newUser) {
        return User.builder()
                .PESELNumber(newUser.PESELNumber())
                .email(newUser.email())
                .password(newUser.password())
                .firstName(newUser.firstName())
                .lastName(newUser.lastName())
                .phoneNumber(newUser.phoneNumber())
                .build();
    }

}