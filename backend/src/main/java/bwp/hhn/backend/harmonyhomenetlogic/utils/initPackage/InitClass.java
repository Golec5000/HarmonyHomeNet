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
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitClass {

    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;

    @PostConstruct
    public void init() {
        System.out.println("Init class");

        // Create and save 3 users
        User user1 = User.builder()
                .uuidID(UUID.fromString("c6aa83d5-2988-4f7c-9a02-007de1411718"))
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("123456789")
                .role(Role.ADMIN)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .uuidID(UUID.fromString("b4b4b27d-e11f-4aad-ba35-d44d1a9fe35f"))
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password123")
                .phoneNumber("987654321")
                .role(Role.USER)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .uuidID(UUID.fromString("efff2ff3-23bc-4ab0-bd36-68d972b547f4"))
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .password("password123")
                .phoneNumber("555555555")
                .role(Role.USER)
                .build();
        userRepository.save(user3);

        // Create and save 3 apartments
        Apartment apartment1 = Apartment.builder()
                .uuidID(UUID.fromString("0705ed4e-9b24-4001-963c-36ae30c3b6f3"))
                .address("Balonowa 5/10")
                .city("Kraków")
                .zipCode("30-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment1);

        Apartment apartment2 = Apartment.builder()
                .uuidID(UUID.fromString("40327178-a4f2-44e9-8401-840cac30eea3"))
                .address("Zakopiańska 12/100")
                .city("Kraków")
                .zipCode("00-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment2);

        Apartment apartment3 = Apartment.builder()
                .uuidID(UUID.fromString("4ab5390c-3381-4989-a967-2aa8be135955"))
                .address("Kwiatowa 1/1")
                .city("Kraków")
                .zipCode("80-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment3);
    }
}