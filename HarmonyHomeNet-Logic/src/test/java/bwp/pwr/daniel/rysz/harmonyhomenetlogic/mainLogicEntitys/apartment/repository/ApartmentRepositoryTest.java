package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository.BuildingRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.testConfig.PostgresqlTestContainer;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = PostgresqlTestContainer.class)
class ApartmentRepositoryTest {

    @Autowired
    private PostgreSQLContainer<?> postgreSQLContainer;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private BuildingRepository buildingRepository;


    @BeforeEach
    void setUp() {
        // Clear the database
        apartmentRepository.deleteAll();
    }

    @Test
    void checkIfPostgresqlContainerIsNotNull() {
        assertThat(postgreSQLContainer).isNotNull();
    }

    @Test
    void checkIfBuildingRepositoryIsNotNull() {
        assertThat(apartmentRepository).isNotNull();
    }

    @Test
    void checkDatabaseConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void saveApartment() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);

        // Then
        assertThat(savedApartment).isNotNull();
    }

    @Test
    void failSaveApartmentWithNegativeAndZeroApartmentNumber() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(-1)
                .area(new BigDecimal(100))
                .build();

        // Then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> apartmentRepository.save(apartment));

        // Given
        Apartment apartment2 = Apartment.builder()
                .apartmentNumber(0)
                .area(new BigDecimal(100))
                .build();

        // Then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> apartmentRepository.save(apartment2));
    }

    @Test
    void failSaveApartmentWithNotUniqueApartmentNumber() {
        // Given
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .apartments(new ArrayList<>())
                .build();

        buildingRepository.save(building);

        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .building(building)
                .build();

        building.getApartments().add(apartment);

        apartmentRepository.save(apartment);
        buildingRepository.save(building);

        Apartment apartment2 = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .building(building)
                .build();

        // Then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> apartmentRepository.save(apartment2));
    }

    @Test
    void checkIsAreaNotNegativeOrZero() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(-100))
                .build();

        // Then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> apartmentRepository.save(apartment));

        // Given
        Apartment apartment2 = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(0))
                .build();

        // Then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> apartmentRepository.save(apartment2));

    }

    @Test
    void checkIsAreaIsCorrect() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);

        // Then
        assertThat(savedApartment.getArea()).isEqualTo(new BigDecimal(100));
    }

    @Test
    void findMoreThenOneApartment() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        Apartment apartment2 = Apartment.builder()
                .apartmentNumber(2)
                .area(new BigDecimal(100))
                .build();

        // When

        apartmentRepository.save(apartment);
        apartmentRepository.save(apartment2);

        // Then

        assertThat(apartmentRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void checkIfThereAreNoApartments() {
        //When
        List<Apartment> apartments = apartmentRepository.findAll();

        //Then
        assertThat(apartments.size()).isEqualTo(0);
    }

    @Test
    void findApartmentById() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);

        // Then
        Apartment foundApartment = apartmentRepository.findById(savedApartment.getId()).orElse(null);
        assertThat(foundApartment).isNotNull();
    }

    @Test
    void failFindApartmentById() {
        // given
        UUID wrongID = UUID.randomUUID();

        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);
        while (savedApartment.getId().equals(wrongID)) {
            wrongID = UUID.randomUUID();
        }

        // Then
        Apartment foundApartment = apartmentRepository.findById(wrongID).orElse(null);
        assertThat(foundApartment).isNull();
    }

    @Test
    void deleteApartmentById() {
        // Given
        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);

        // Then
        assertThat(apartmentRepository.existsById(savedApartment.getId())).isTrue();
        apartmentRepository.deleteById(savedApartment.getId());

        assertThat(apartmentRepository.findById(savedApartment.getId()).isEmpty()).isTrue();
    }

    @Test
    void failDeleteApartmentById() {
        // Given
        UUID wrongID = UUID.randomUUID();

        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .build();

        // When
        Apartment savedApartment = apartmentRepository.save(apartment);

        while (savedApartment.getId().equals(wrongID)) {
            wrongID = UUID.randomUUID();
        }

        // Then
        assertThat(apartmentRepository.existsById(wrongID)).isFalse();

    }

    @Test
    void findByApartmentNumberAndBuildingId() {
        // Given
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .apartments(new ArrayList<>())
                .build();

        buildingRepository.save(building);

        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .building(building)
                .build();

        building.getApartments().add(apartment);

        apartmentRepository.save(apartment);
        buildingRepository.save(building);

        // When
        Apartment foundApartment = apartmentRepository.findByApartmentNumberAndBuildingId(1, building.getId()).orElse(null);

        // Then
        assertThat(foundApartment).isNotNull();
    }

    @Test
    void failFindByApartmentNumberAndBuildingId() {
        // Given
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .apartments(new ArrayList<>())
                .build();

        buildingRepository.save(building);

        Apartment apartment = Apartment.builder()
                .apartmentNumber(1)
                .area(new BigDecimal(100))
                .building(building)
                .build();

        building.getApartments().add(apartment);

        apartmentRepository.save(apartment);
        buildingRepository.save(building);

        // When
        Apartment foundApartment = apartmentRepository.findByApartmentNumberAndBuildingId(2, building.getId()).orElse(null);

        // Then
        assertThat(foundApartment).isNull();
    }
}