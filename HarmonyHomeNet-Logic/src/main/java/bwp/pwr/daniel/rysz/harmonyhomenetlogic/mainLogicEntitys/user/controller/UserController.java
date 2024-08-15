package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Employee;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service.UserService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Gender;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bwp/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/user-by-id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) throws UserNotFoundException {
        return userService.findById(UUID.fromString(userId))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));
    }

    @GetMapping("/user-by-login/{userLogin}")
    public ResponseEntity<User> getUserByLogin(@PathVariable String userLogin) throws UserNotFoundException {
        return userService.findByLogin(userLogin)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("wrong user login"));
    }

    @GetMapping("/user-by-email/{userEmail}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String userEmail) throws UserNotFoundException {
        return userService.findByEmail(userEmail)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("wrong user email"));
    }

    @GetMapping("/user-by-PESELNumber/{userPESELNumber}")
    public ResponseEntity<User> getUserByPESELNumber(@PathVariable String userPESELNumber) throws UserNotFoundException {
        return userService.findByPESELNumber(userPESELNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("wrong user PESELNumber"));
    }

    @PostMapping("/add-resident")
    public ResponseEntity<Resident> addResident(@RequestBody UserRequest newUser) {
        Resident resident = Resident.builder()
                .PESELNumber(newUser.getPESELNumber())
                .email(newUser.getEmail())
                .password(newUser.getPassword())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .phoneNumber(newUser.getPhoneNumber())
                .build();
        userService.save(resident);
        return ResponseEntity.ok(resident);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<User> addEmployee(@RequestBody UserRequest newUser) {
        Employee employee = Employee.builder()
                .PESELNumber(newUser.getPESELNumber())
                .email(newUser.getEmail())
                .password(newUser.getPassword())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .phoneNumber(newUser.getPhoneNumber())
                .build();
        userService.save(employee);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<User> updateResident(@PathVariable String userId, @RequestBody UserRequest updatedUser) throws UserNotFoundException {
        User updateUser = userService.findById(UUID.fromString(userId))
                .map(user -> {
                    user.setPESELNumber(updatedUser.getPESELNumber() != null ? updatedUser.getPESELNumber() : user.getPESELNumber());
                    user.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : user.getEmail());
                    user.setPassword(updatedUser.getPassword() != null ? updatedUser.getPassword() : user.getPassword());
                    user.setFirstName(updatedUser.getFirstName() != null ? updatedUser.getFirstName() : user.getFirstName());
                    user.setLastName(updatedUser.getLastName() != null ? updatedUser.getLastName() : user.getLastName());
                    user.setPhoneNumber(updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : user.getPhoneNumber());
                    user.setUserGender(updatedUser.getGender() != null ? Gender.valueOf(updatedUser.getGender()) : user.getUserGender());
                    return user;
                })
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));
        userService.save(updateUser);
        return ResponseEntity.ok(updateUser);
    }

    @PutMapping("/update-user-role/{userId}")
    public ResponseEntity<User> updateUserRole(@PathVariable String userId, @RequestBody UserRequest updatedUser) throws UserNotFoundException {
        User updateUser = userService.findById(UUID.fromString(userId))
                .map(user -> {
                    user.setRole(updatedUser.getRole() != null ? updatedUser.getRole().stream().map(Role::valueOf).collect(Collectors.toSet()) : user.getRole());
                    return user;
                })
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));
        userService.save(updateUser);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<User> deleteUserById(@PathVariable String userId) {
        userService.deleteById(UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }
}