package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.ForumRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service.ParkingSpaceService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Employee;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInit {

    private final BuildingService buildingService;
    private final BasementService basementService;
    private final ParkingSpaceService parkingSpaceService;

    private final ApartmentService apartmentService;
    private final UserRepository userRepository;

    private final ForumRepository forumRepository;

    @PostConstruct
    @Transactional
    public void init() {

        // Sprawdzenie, czy tabela Resident jest pusta
        if (userRepository.count() > 0) {
            log.info("Database already initialized. Skipping data creation.");
            return; // Jeśli są już dane, przerywamy wykonanie metody.
        }

        for (int i = 0; i < 20; i++) {

            if (i % 2 == 0) {
                Resident resident = Resident.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .email("user" + i + "@gmail.com")
                        .password("password" + i)
                        .build();
                userRepository.save(resident);

            } else {
                Employee employee = Employee.builder()
                        .firstName("Grzesiek")
                        .lastName("Nowak")
                        .email("user" + i + "@gmail.com")
                        .password("password" + i)
                        .build();
                userRepository.save(employee);
            }


        }

        for (int i = 0; i < 10; i++) {
            Building buildingToSave = Building.builder()
                    .buildingName("Blok " + (i + 1))
                    .street("ul. Długa " + i)
                    .city("Kraków")
                    .region("Małopolskie")
                    .build();

            buildingService.save(buildingToSave);

            List<Basement> basements = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                Basement basement = Basement.builder()
                        .area(BigDecimal.valueOf((new Random().nextInt(15) + 6) * (j + 1) * new Random().nextDouble()))
                        .basementNumber(j + 1)
                        .building(buildingToSave)
                        .build();

                basementService.save(basement);
                basements.add(basement);
            }

            List<ParkingSpace> parkingSpaces = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                ParkingSpace parkingSpace = ParkingSpace.builder()
                        .number(j + 1)
                        .building(buildingToSave)
                        .build();

                parkingSpaceService.save(parkingSpace);
                parkingSpaces.add(parkingSpace);
            }

            // New code for adding apartments
            List<Apartment> apartments = new ArrayList<>();
            for (int k = 0; k < 10; k++) {
                Apartment apartment = Apartment.builder()
                        .apartmentNumber(k + 1)
                        .area(BigDecimal.valueOf(50.0 + (k + 1) * 5.0 * new Random().nextDouble())) // Example area calculation
                        .building(buildingToSave)
                        .build();

                apartmentService.save(apartment);
                apartments.add(apartment);
            }

            buildingToSave.setParkingSpaces(parkingSpaces);
            buildingToSave.setBasements(basements);
            // Assuming Building entity has a field to reference apartments
            buildingToSave.setApartments(apartments);
            buildingService.save(buildingToSave); // update building with basements, parking spaces, and apartments
        }

        for (int i = 0; i < 2; i++) {
            Forum forum = Forum.builder()
                    .forumDescription("Test desc." + (i + 1))
                    .forumName("Forum" + (i + 1))
                    .build();

            forumRepository.save(forum);
        }
    }
}