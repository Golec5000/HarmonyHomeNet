package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Building;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingService {
    List<Building> findAll();
    Optional<Building> findById(UUID id);
    void save(Building building);
    String deleteById(UUID id);
    Optional<Building> findByBuildingName(String name);
}
