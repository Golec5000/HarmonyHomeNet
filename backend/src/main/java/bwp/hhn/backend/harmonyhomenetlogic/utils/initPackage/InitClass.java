package bwp.hhn.backend.harmonyhomenetlogic.utils.initPackage;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class InitClass {

    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;

    @PostConstruct
    public void init() {
        System.out.println("Init class");

        createUserIfNotExists("John", "Doe", "john.doe@example.com", "password123", "123456789", Role.ADMIN);
        createUserIfNotExists("Jane", "Smith", "jane.smith@example.com", "password123", "987654321", Role.USER);
        createUserIfNotExists("Alice", "Johnson", "alice.johnson@example.com", "password123", "555555555", Role.USER);

        createApartment("Balonowa 5/10", "Kraków", "30-000", "SIG001");
        createApartment("Zakopiańska 12/100", "Kraków", "00-000", "SIG002");
        createApartment("Kwiatowa 1/1", "Kraków", "80-000", "SIG003");
    }

    private void createUserIfNotExists(String firstName, String lastName, String email, String password, String phoneNumber, Role role) {
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
            userRepository.save(user);
        }
    }

    private void createApartment(String address, String city, String zipCode, String signature) {
        Optional<Apartment> existingApartments = apartmentsRepository.findByApartmentSignature(signature);
        if (existingApartments.isEmpty()) {
            Apartment apartment = Apartment.builder()
                    .address(address)
                    .city(city)
                    .zipCode(zipCode)
                    .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                    .apartmentPercentValue(BigDecimal.valueOf(50.00))
                    .apartmentSignature(signature)
                    .build();
            apartmentsRepository.save(apartment);
        }
    }
}