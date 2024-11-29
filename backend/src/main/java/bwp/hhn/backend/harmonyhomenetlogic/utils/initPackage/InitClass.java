package bwp.hhn.backend.harmonyhomenetlogic.utils.initPackage;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitClass {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${SUPER_ADMIN_FIRST_NAME}")
    private String superAdminFirstName;

    @Value("${SUPER_ADMIN_LAST_NAME}")
    private String superAdminLastName;

    @Value("${SUPER_ADMIN_EMAIL}")
    private String superAdminEmail;

    @Value("${SUPER_ADMIN_PASSWORD}")
    private String superAdminPassword;

    @Value("${SUPER_ADMIN_PHONE}")
    private String superAdminPhone;

    @PostConstruct
    public void init() {
        System.out.println("Init class");

        User SA = createUserIfNotExists(
                superAdminFirstName,
                superAdminLastName,
                superAdminEmail,
                bCryptPasswordEncoder.encode(superAdminPassword),
                superAdminPhone,
                Role.ROLE_SUPER_ADMIN
        );
    }

    private User createUserIfNotExists(String firstName, String lastName, String email, String password, String phoneNumber, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .role(role)
                    .build();
            return userRepository.save(user);
        }
        return existingUser.get();
    }
}