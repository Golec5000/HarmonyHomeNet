package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.auth.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.auth.service.AuthService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.LoginRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.RegisterRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.LoginResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bwp/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PutMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


}
