package bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
    Optional<Building> findByName(String name);
    void deleteById(UUID id);
    Optional<Building> findById(UUID id);
}
