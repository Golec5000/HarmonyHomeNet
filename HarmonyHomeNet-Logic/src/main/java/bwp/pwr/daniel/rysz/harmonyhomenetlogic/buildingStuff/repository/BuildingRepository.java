package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Optional<Building> findByBuildingName(String buildingName);
}
