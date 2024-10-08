package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service.ParkingSpaceService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BuildingRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ParkingSpaceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/api/v1/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final BasementService basementService;
    private final ParkingSpaceService parkingSpaceService;

    @GetMapping("/all")
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.findAll());
    }

    @GetMapping("/building-by-id/{building_id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable String building_id) throws BuildingNotFoundException {
        UUID id = UUID.fromString(building_id);

        return buildingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));
    }

    @GetMapping("/building-by-name/{building_name}")
    public ResponseEntity<Building> getBuildingByName(@PathVariable String building_name) throws BuildingNotFoundException {
        return buildingService.findByBuildingName(building_name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building name"));
    }

    @GetMapping("/buildings-by-region/{building_region}")
    public ResponseEntity<List<Building>> getAllBuildingByRegion(@PathVariable String building_region) {
        List<Building> listByRegion = buildingService.findAll().stream()
                .filter(building -> building.getRegion().equals(building_region))
                .toList();
        return ResponseEntity.ok(listByRegion);
    }

    @PutMapping("/add-building")
    public ResponseEntity<Building> addNewBuilding(@RequestBody BuildingRequest buildingRequest) {
        Building building = Building.builder()
                .buildingName(buildingRequest.getBuildingName())
                .city(buildingRequest.getCity())
                .street(buildingRequest.getStreet())
                .region(buildingRequest.getRegion())
                .build();
        buildingService.save(building);
        return ResponseEntity.ok(building);
    }

    @PostMapping("/building-id/{building_id}/add-apartment")
    public ResponseEntity<Building> addApartmentToBuilding(@PathVariable String building_id, @RequestBody ApartmentRequest apartmentRequest) throws BuildingNotFoundException {

        UUID id = UUID.fromString(building_id);

        Building buildingToUpdate = buildingService.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Apartment newApartment = Apartment.builder()
                .apartmentNumber(apartmentRequest.getApartmentNumber())
                .area(apartmentRequest.getArea())
                .build();

        if (buildingToUpdate.getApartments() == null) buildingToUpdate.setApartments(new ArrayList<>());
        buildingToUpdate.getApartments().add(newApartment);

        apartmentService.save(newApartment);
        buildingService.save(buildingToUpdate);

        return ResponseEntity.ok(buildingToUpdate);

    }

    @PostMapping("/building-id/{building_id}/add-basement")
    public ResponseEntity<Building> addBasementToBuilding(@PathVariable String building_id, @RequestBody BasementRequest basementRequest) throws BuildingNotFoundException {
        UUID id = UUID.fromString(building_id);

        Building buildingToUpdate = buildingService.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Basement newBasement = Basement.builder()
                .basementNumber(basementRequest.getBasementNumber())
                .area(basementRequest.getArea())
                .build();

        if (buildingToUpdate.getApartments() == null) buildingToUpdate.setBasements(new ArrayList<>());
        buildingToUpdate.getBasements().add(newBasement);

        basementService.save(newBasement);
        buildingService.save(buildingToUpdate);

        return ResponseEntity.ok(buildingToUpdate);
    }

    @PostMapping("/building-id/{building_id}/add-parking-space")
    public ResponseEntity<Building> addParkingSpaceToBuilding(@PathVariable String building_id, @RequestBody ParkingSpaceRequest parkingSpaceRequest) throws BuildingNotFoundException {
        UUID id = UUID.fromString(building_id);

        Building buildingToUpdate = buildingService.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        ParkingSpace parkingSpace = ParkingSpace.builder()
                .number(parkingSpaceRequest.getNumber())
                .build();

        if (buildingToUpdate.getParkingSpaces() == null) buildingToUpdate.setParkingSpaces(new ArrayList<>());
        buildingToUpdate.getParkingSpaces().add(parkingSpace);

        parkingSpaceService.save(parkingSpace);
        buildingService.save(buildingToUpdate);

        return ResponseEntity.ok(buildingToUpdate);
    }

    @DeleteMapping("/remove-building/{building_id}")
    public ResponseEntity<Building> removeBuildingById(@PathVariable String building_id) {
        UUID id = UUID.fromString(building_id);
        buildingService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}