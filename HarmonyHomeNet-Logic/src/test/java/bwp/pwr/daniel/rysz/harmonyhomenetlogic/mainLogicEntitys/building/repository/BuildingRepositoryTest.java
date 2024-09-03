package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.testConfig.PostgresqlTestContainer;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = PostgresqlTestContainer.class)
public class BuildingRepositoryTest {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private PostgreSQLContainer<?> postgreSQLContainer;

    @BeforeEach
    void setUp() {
        // Clear the database
        buildingRepository.deleteAll();
    }

    @Test
    void checkIfPostgresqlContainerIsNotNull() {
        assertThat(postgreSQLContainer).isNotNull();
    }

    @Test
    void checkIfBuildingRepositoryIsNotNull() {
        assertThat(buildingRepository).isNotNull();
    }

    @Test
    void checkDatabaseConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void findMoreThenOneBuilding() {
        // given
        Building building1 = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        System.out.println(building1.getId());

        Building building2 = Building.builder()
                .buildingName("TestBuilding-2")
                .region(Region.MAZOWIECKIE)
                .city("Warszawa")
                .street("Testowa -2")
                .build();

        // when
        buildingRepository.save(building1);
        buildingRepository.save(building2);

        // then
        List<Building> buildings = buildingRepository.findAll();
        assertThat(buildings.size()).isEqualTo(2);
    }

    @Test
    void checkIfThereAnyBuildingInDatabase() {
        // when
        List<Building> buildings = buildingRepository.findAll();

        // then
        assertThat(buildings.size()).isEqualTo(0);
    }

    @Test
    void findBuildingById() {
        // given
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        Building savedBuilding = buildingRepository.save(building);

        // then
        Building buildingById = buildingRepository.findById(savedBuilding.getId()).orElse(null);
        assertThat(buildingById).isNotNull();
        assertThat(Objects.requireNonNull(buildingById).getBuildingName()).isEqualTo(building.getBuildingName());
    }

    @Test
    void failFindBuildingById() {
        // given
        UUID wrongID = UUID.randomUUID();

        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        // when
        Building saveBUilding = buildingRepository.save(building);

        while (saveBUilding.getId().equals(wrongID)) {
            wrongID = UUID.randomUUID();
        }

        // then
        Building buildingById = buildingRepository.findById(wrongID).orElse(null);

        assertThat(buildingById).isNull();
    }

    @Test
    void deleteBuildingById() {
        // given
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        // when
        Building saveBuilding = buildingRepository.save(building);

        // then
        assertThat(buildingRepository.existsById(saveBuilding.getId())).isTrue();
        buildingRepository.deleteById(saveBuilding.getId());

        Building buildingById = buildingRepository.findById(saveBuilding.getId()).orElse(null);
        assertThat(buildingById).isNull();
    }

    @Test
    void failDeleteBuildingById() {
        // given
        UUID wrongID = UUID.randomUUID();

        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        // when
        Building savedBuilding = buildingRepository.save(building);

        // Ensure wrongID is not the same as the saved building's ID
        while (savedBuilding.getId().equals(wrongID)) {
            wrongID = UUID.randomUUID();
        }

        // then
        assertThat(buildingRepository.existsById(wrongID)).isFalse();

    }

    @Test
    void checkIfNewBuildingIsSaved() {

        // given
        Building building = Building.builder()
                .buildingName("TestBuilding-2")
                .region(Region.MAZOWIECKIE)
                .city("Warszawa")
                .street("Testowa -2")
                .build();

        // when
        Building savedBuilding = buildingRepository.save(building);


        // then
        assertThat(savedBuilding).isNotNull();
        assertThat(savedBuilding.getBuildingName()).isEqualTo(building.getBuildingName());
        assertThat(savedBuilding.getRegion()).isEqualTo(building.getRegion());
    }

    @Test
    void successFindBuildingByName() {
        // given
        String buildingName = "TestBuilding-1";

        // Reinitialize the database
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();
        buildingRepository.save(building);

        // when
        Building saveBuilding = buildingRepository.findByBuildingName(buildingName).orElse(null);

        // then
        assertThat(saveBuilding).isNotNull();
        assertThat(Objects.requireNonNull(saveBuilding).getBuildingName()).isEqualTo(buildingName);
    }

    @Test
    void failFindBuildingByName() {
        // given
        String buildingName = "TestBuilding-null";

        // when
        Building building = buildingRepository.findByBuildingName(buildingName).orElse(null);

        // then
        assertThat(building).isNull();
    }

    @Test
    void successFindAllByRegion() {

        // Reinitialize the database
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();
        buildingRepository.save(building);

        // given
        Region region = Region.LUBELSKIE;

        // when
        List<Building> buildings = buildingRepository.findAllByRegion(region);

        // then
        assertThat(buildings).isNotNull();
        assertThat(buildings.get(0).getRegion()).isEqualTo(region);
    }

    @Test
    void failFindAllByRegion() {
        // given
        Region region = Region.MAZOWIECKIE;

        // Reinitialize the database
        Building building = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();
        buildingRepository.save(building);

        // when
        List<Building> buildings = buildingRepository.findAllByRegion(region);

        // then
        assertThat(buildings.size()).isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenSavingBuildingWithDuplicateName() {
        // given
        Building building1 = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.LUBELSKIE)
                .city("Lublin")
                .street("Testowa -1")
                .build();

        Building building2 = Building.builder()
                .buildingName("TestBuilding-1")
                .region(Region.MAZOWIECKIE)
                .city("Warszawa")
                .street("Testowa -2")
                .build();

        // when
        buildingRepository.save(building1);

        // then
        assertThrows(DataIntegrityViolationException.class, () -> {
            buildingRepository.saveAndFlush(building2);
        });
    }

}