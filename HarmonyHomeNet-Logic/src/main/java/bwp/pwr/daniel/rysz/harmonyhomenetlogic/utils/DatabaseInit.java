package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.ForumRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.PostRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.TopicRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service.ParkingSpaceService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.ResidentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
    private final ResidentRepository residentRepository;

    private final ForumRepository forumRepository;
    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    @PostConstruct
    public void init() {

        try {
            for (int i = 0; i < 10; i++) {
                Resident resident = Resident.builder()
                        .login("user" + i)
                        .PESELNumber(PeselGenerator(i))
                        .email("user" + i + "@gmail.com")
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
        } catch (Exception e) {
            log.error("Error while initializing database: {}", e.getMessage());
        }
    }

    private String PeselGenerator(int index) {
        return List.of(
                "00212771198",
                "01271849549",
                "78091078723",
                "72111861654",
                "69072751847",
                "93102344147",
                "67061148845",
                "82061159354",
                "49070653334",
                "60102554238"
        ).get(index);
    }
}