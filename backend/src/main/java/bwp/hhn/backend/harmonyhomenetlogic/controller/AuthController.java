package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AuthService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PasswordChangeRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PasswordResetRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PasswordUpdateRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.RegisterRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bwp/hhn/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(Authentication authentication) {
        return ResponseEntity.ok(authService.login(authentication));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest userRequest,@RequestParam String accessToken) {
        return ResponseEntity.ok(authService.register(userRequest, accessToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordUpdateRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        authService.changePassword(request.getNewPassword(), request.getConfirmPassword(), request.getEmail());
        return ResponseEntity.ok("Password has been changed successfully.");
    }

}
