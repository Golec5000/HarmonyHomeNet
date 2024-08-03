package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingServiceImp implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    @Override
    public Optional<Building> findById(UUID id) {
        return buildingRepository.findById(id);
    }

    @Override
    public void save(Building building) {
        buildingRepository.save(building);
    }

    @Override
    public String deleteById(UUID id) {
        buildingRepository.deleteById(id);
        return "Building with id: " + id + " has been deleted";
    }

    @Override
    public Optional<Building> findByBuildingName(String buildingName) {
        return buildingRepository.findByBuildingName(buildingName);
    }

}
