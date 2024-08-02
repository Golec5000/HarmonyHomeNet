package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services.ParkingSpaceService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.resident.entitys.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.resident.repository.ResidentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DatabaseInit {

    private final BuildingService buildingService;
    private final BasementService basementService;
    private final ParkingSpaceService parkingSpaceService;

    private final ApartmentService apartmentService;

    private final ResidentRepository residentRepository;

    @PostConstruct
    public void init() {

        for (int i = 0; i < 20; i++) {
            Resident resident = Resident.builder()
                    .login("user" + i)
                    .PESELNumber(PeselGenerator())
                    .email("user" + i + "@gmail.com")
                    .residentType(GenerateType())
                    .build();
            residentRepository.save(resident);
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
                        .area(BigDecimal.valueOf((new Random().nextInt(15) + 6) * (j + 1)))
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
                        .area(BigDecimal.valueOf(50.0 + k * 5.0 * new Random().nextDouble())) // Example area calculation
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
    }

    private List<ResidentType> GenerateType() {
        switch (new Random().nextInt(4)) {
            case 1 -> {
                return List.of(ResidentType.TENANT);
            }
            case 2 -> {
                return List.of(ResidentType.OWNER);
            }
            case 3 -> {
                return List.of(ResidentType.TENANT, ResidentType.OWNER);
            }
            default -> {
                return null;
            }
        }
    }

    private String PeselGenerator() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) sb.append(new Random().nextInt(10));
        return sb.toString();
    }

}
