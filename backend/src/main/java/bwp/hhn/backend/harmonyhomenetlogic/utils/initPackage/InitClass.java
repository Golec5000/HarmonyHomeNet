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

@Component
@RequiredArgsConstructor
public class InitClass {

    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;

    @PostConstruct
    public void init() {
        System.out.println("Init class");

        User user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("123456789")
                .role(Role.ADMIN)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password123")
                .phoneNumber("987654321")
                .role(Role.USER)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .password("password123")
                .phoneNumber("555555555")
                .role(Role.USER)
                .build();
        userRepository.save(user3);

        Apartment apartment1 = Apartment.builder()
                .address("Balonowa 5/10")
                .city("Kraków")
                .zipCode("30-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment1);

        Apartment apartment2 = Apartment.builder()
                .address("Zakopiańska 12/100")
                .city("Kraków")
                .zipCode("00-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment2);

        Apartment apartment3 = Apartment.builder()
                .address("Kwiatowa 1/1")
                .city("Kraków")
                .zipCode("80-000")
                .apartmentArea(BigDecimal.valueOf(new Random().nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
                .apartmentPercentValue(BigDecimal.valueOf(50.00))
                .build();
        apartmentsRepository.save(apartment3);
    }
}