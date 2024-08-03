package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.BuildingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final BasementService basementService;

    @GetMapping("/v1/all")
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.findAll());
    }

    @GetMapping("/v1/building-by-id/{building_id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable String building_id) {
        try {
            UUID id = UUID.fromString(building_id);

            return buildingService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(ResponseEntity.notFound()::build);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/v1/building-by-name/{building_name}")
    public ResponseEntity<Building> getBuildingByName(@PathVariable String building_name) {
        return buildingService.findByBuildingName(building_name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("/v1/buildings-by-region/{building_region}")
    public ResponseEntity<List<Building>> getAllBuildingByRegion(@PathVariable String building_region) {
        List<Building> listByRegion = buildingService.findAll().stream()
                .filter(building -> building.getRegion().equals(building_region))
                .toList();
        return ResponseEntity.ok(listByRegion);
    }

    @PutMapping("v1/add-building")
    public ResponseEntity<Building> addNewBuilding(@RequestBody BuildingRequest buildingRequest) {
        Building building = Building.builder()
                .buildingName(buildingRequest.getBuildingName())
                .street(buildingRequest.getStreet())
                .region(buildingRequest.getRegion())
                .build();
        buildingService.save(building);
        return ResponseEntity.ok(building);
    }

    @PostMapping("/v1/building-id/{building_id}/add-apartment")
    public ResponseEntity<Building> addApartmentToBuilding(@PathVariable String building_id, @RequestBody ApartmentRequest apartmentRequest) {

        try {
            UUID id = UUID.fromString(building_id);

            Building buildingToUpdate = buildingService.findById(id).orElseThrow(IllegalArgumentException::new);

            Apartment newApartment = Apartment.builder()
                    .apartmentNumber(apartmentRequest.getApartmentNumber())
                    .area(apartmentRequest.getArea())
                    .build();

            if (buildingToUpdate.getApartments() == null) buildingToUpdate.setApartments(new ArrayList<>());
            buildingToUpdate.getApartments().add(newApartment);

            apartmentService.save(newApartment);
            buildingService.save(buildingToUpdate);

            return ResponseEntity.ok(buildingToUpdate);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/v1/building-id/{building_id}/add-basement")
    public ResponseEntity<Building> addBasementToBuilding(@PathVariable String building_id, @RequestBody BasementRequest basementRequest){
        try {
            UUID id = UUID.fromString(building_id);

            Building buildingToUpdate = buildingService.findById(id).orElseThrow(IllegalArgumentException::new);

            Basement newBasement = Basement.builder()
                    .basementNumber(basementRequest.getBasementNumber())
                    .area(basementRequest.getArea())
                    .build();

            if (buildingToUpdate.getApartments() == null) buildingToUpdate.setBasements(new ArrayList<>());
            buildingToUpdate.getBasements().add(newBasement);

            basementService.save(newBasement);
            buildingService.save(buildingToUpdate);

            return ResponseEntity.ok(buildingToUpdate);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}