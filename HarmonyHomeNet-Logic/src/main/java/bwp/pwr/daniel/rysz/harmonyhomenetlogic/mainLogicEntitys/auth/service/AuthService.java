package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.auth.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.infrastructure.auth.JwtService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.UserRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.LoginRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.userStaff.RegisterRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.LoginResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .role(Set.of(Role.USER))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return RegisterResponse.builder()
                .token(jwtToken)
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest) throws UserNotFoundException{
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLogin(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findUserByLogin(loginRequest.getLogin())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .token(jwtToken)
                .build();
    }
}
