package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BuildingRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ParkingSpaceRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BuildingResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/api/v1/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/all")
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        List<BuildingResponse> buildings = buildingService.findAll();
        return buildings.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(buildings);
    }

    @GetMapping("/building-by-id/{building_id}")
    public ResponseEntity<BuildingResponse> getBuildingById(@PathVariable String building_id) {
        return ResponseEntity.ok(buildingService.findById(UUID.fromString(building_id)));
    }

    @GetMapping("/building-by-name/{building_name}")
    public ResponseEntity<BuildingResponse> getBuildingByName(@PathVariable String building_name) {
        return ResponseEntity.ok(buildingService.findByBuildingName(building_name));
    }

    @GetMapping("/buildings-by-region/{building_region}")
    public ResponseEntity<List<BuildingResponse>> getAllBuildingByRegion(@PathVariable String building_region) {
        List<BuildingResponse> buildings = buildingService.findAllByRegion(building_region);
        return buildings.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(buildings);
    }

    @GetMapping("/buildings-id/{building_id}/apartments")
    public ResponseEntity<List<ApartmentResponse>> getAllApartmentsFromBuilding(@PathVariable String building_id) {
        List<ApartmentResponse> apartments = buildingService.findAllApartmentsFromBuilding(UUID.fromString(building_id));
        return apartments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(apartments);
    }

    @PostMapping("/add-building")
    public ResponseEntity<BuildingResponse> addNewBuilding(@RequestBody BuildingRequest buildingRequest) {
        return ResponseEntity.created(null).body(buildingService.save(getBuildingFromRequest(buildingRequest)));
    }

    @PutMapping("/building-id/{building_id}/add-apartment")
    public ResponseEntity<ApartmentResponse> addApartmentToBuilding(@PathVariable String building_id, @RequestBody ApartmentRequest apartmentRequest) {
        return ResponseEntity.created(null).body(buildingService.addApartmentToBuilding(UUID.fromString(building_id), apartmentRequest));
    }

    @PutMapping("/building-id/{building_id}/add-basement")
    public ResponseEntity<BasementResponse> addBasementToBuilding(@PathVariable String building_id, @RequestBody BasementRequest basementRequest) {
        return ResponseEntity.created(null).body(buildingService.addBasementToBuilding(UUID.fromString(building_id), basementRequest));
    }

    @PutMapping("/building-id/{building_id}/add-parking-space")
    public ResponseEntity<ParkingSpaceResponse> addParkingSpaceToBuilding(@PathVariable String building_id, @RequestBody ParkingSpaceRequest parkingSpaceRequest) {
        return ResponseEntity.created(null).body(buildingService.addParkingSpaceToBuilding(UUID.fromString(building_id), parkingSpaceRequest));
    }

    @DeleteMapping("/remove-building/{building_id}")
    public ResponseEntity<String> removeBuildingById(@PathVariable String building_id) {
        buildingService.deleteById(UUID.fromString(building_id));
        return ResponseEntity.ok("Building remove complete");
    }

    private Building getBuildingFromRequest(BuildingRequest buildingRequest) {
        return Building.builder()
                .buildingName(buildingRequest.buildingName())
                .region(buildingRequest.region())
                .city(buildingRequest.city())
                .street(buildingRequest.street())
                .build();
    }
}