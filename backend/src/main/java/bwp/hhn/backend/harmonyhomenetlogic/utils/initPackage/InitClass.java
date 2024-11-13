package bwp.hhn.backend.harmonyhomenetlogic.utils.initPackage;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class InitClass {

    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {
        System.out.println("Init class");

        User john = createUserIfNotExists("John", "Doe", "john.doe@example.com", bCryptPasswordEncoder.encode("password123"), "123456789", Role.ROLE_ADMIN);
        User jane = createUserIfNotExists("Jane", "Smith", "jane.smith@example.com", bCryptPasswordEncoder.encode("password123"), "987654321", Role.ROLE_OWNER);
        User alice = createUserIfNotExists("Alice", "Johnson", "alice.johnson@example.com", bCryptPasswordEncoder.encode("password123"), "555555555", Role.ROLE_OWNER);

        Apartment apartment1 = createApartment("Balonowa 5/10", "Kraków", "30-000", "SIG001");
        Apartment apartment2 = createApartment("Zakopiańska 12/100", "Kraków", "00-000", "SIG002");
        Apartment apartment3 = createApartment("Kwiatowa 1/1", "Kraków", "80-000", "SIG003");

        createPossessionHistory(jane, apartment1);
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

    private Apartment createApartment(String address, String city, String zipCode, String signature) {
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
            return apartmentsRepository.save(apartment);
        }
        return existingApartments.get();
    }

    private void createPossessionHistory(User user, Apartment apartment) {
        if (possessionHistoryRepository.existsByUserAndApartment(user, apartment)) {
            return;
        }
        PossessionHistory possessionHistory = PossessionHistory.builder()
                .user(user)
                .apartment(apartment)
                .startDate(Instant.now())
                .build();
        possessionHistoryRepository.save(possessionHistory);
    }
}