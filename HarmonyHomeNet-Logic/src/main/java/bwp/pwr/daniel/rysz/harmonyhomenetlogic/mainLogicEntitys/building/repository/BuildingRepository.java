package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Optional<Building> findByBuildingName(String buildingName);
}
