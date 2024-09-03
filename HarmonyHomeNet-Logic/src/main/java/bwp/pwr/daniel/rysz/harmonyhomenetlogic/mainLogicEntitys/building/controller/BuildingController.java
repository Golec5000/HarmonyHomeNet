package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service.BuildingService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BuildingRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ParkingSpaceRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BuildingResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;
import lombok.NonNull;
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
        List<BuildingResponse> buildings = buildingService.mapBuildingListToBuildingResponseList(buildingService.findAll());
        return buildings.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(buildings);
    }

    @GetMapping("/building-by-id/{building_id}")
    public ResponseEntity<BuildingResponse> getBuildingById(@PathVariable String building_id) {
        return ResponseEntity.ok(buildingService.mapBuildingToBuildingResponse(buildingService.findById(UUID.fromString(building_id))));
    }

    @GetMapping("/building-by-name/{building_name}")
    public ResponseEntity<BuildingResponse> getBuildingByName(@PathVariable String building_name) {
        return ResponseEntity.ok(buildingService.mapBuildingToBuildingResponse(buildingService.findByBuildingName(building_name)));
    }

    @GetMapping("/buildings-by-region/{building_region}")
    public ResponseEntity<List<BuildingResponse>> getAllBuildingByRegion(@PathVariable String building_region) {
        List<BuildingResponse> buildings = buildingService.mapBuildingListToBuildingResponseList(buildingService.findAllByRegion(Region.valueOf(building_region)));
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

    @PutMapping("/building-id/{building_id}/remove-apartment/{apartment_id}")
    public ResponseEntity<String> removeApartmentFromBuilding(@PathVariable String building_id, @PathVariable String apartment_id) {
        buildingService.deleteApartmentFromBuilding(UUID.fromString(building_id), UUID.fromString(apartment_id));
        return ResponseEntity.ok("Apartment remove complete");
    }

    @PutMapping("/building-id/{building_id}/remove-basement/{basement_id}")
    public ResponseEntity<String> removeBasementFromBuilding(@PathVariable String building_id, @PathVariable String basement_id) {
        buildingService.deleteBasementFromBuilding(UUID.fromString(building_id), UUID.fromString(basement_id));
        return ResponseEntity.ok("Basement remove complete");
    }

    @PutMapping("/building-id/{building_id}/remove-parking-space/{parking_space_id}")
    public ResponseEntity<String> removeParkingSpaceFromBuilding(@PathVariable String building_id, @PathVariable String parking_space_id) {
        buildingService.deleteParkingSpaceFromBuilding(UUID.fromString(building_id), UUID.fromString(parking_space_id));
        return ResponseEntity.ok("Parking space remove complete");
    }

    @DeleteMapping("/remove-building/{building_id}")
    public ResponseEntity<String> removeBuildingById(@PathVariable String building_id) {
        buildingService.deleteById(UUID.fromString(building_id));
        return ResponseEntity.ok("Building remove complete");
    }

    private Building getBuildingFromRequest(@NonNull BuildingRequest buildingRequest) {
        return Building.builder()
                .buildingName(buildingRequest.buildingName())
                .region(buildingRequest.region())
                .city(buildingRequest.city())
                .street(buildingRequest.street())
                .build();
    }
}