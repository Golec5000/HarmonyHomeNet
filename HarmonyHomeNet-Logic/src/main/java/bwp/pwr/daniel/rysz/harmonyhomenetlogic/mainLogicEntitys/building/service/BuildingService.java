package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ParkingSpaceRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BuildingResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface BuildingService {
    List<BuildingResponse> findAll();

    BuildingResponse findById(UUID id) throws BuildingNotFoundException;

    BuildingResponse save(@NonNull Building newBuilding);

    void deleteById(UUID id) throws BuildingNotFoundException;

    BuildingResponse findByBuildingName(@NonNull String name) throws BuildingNotFoundException;

    List<BuildingResponse> findAllByRegion(@NonNull String region);

    ApartmentResponse addApartmentToBuilding(UUID buildingId, @NonNull ApartmentRequest apartmentRequest) throws BuildingNotFoundException;

    BasementResponse addBasementToBuilding(UUID buildingId, @NonNull BasementRequest basementRequest) throws BuildingNotFoundException;

    ParkingSpaceResponse addParkingSpaceToBuilding(UUID buildingId, @NonNull ParkingSpaceRequest parkingSpaceRequest) throws BuildingNotFoundException;

    List<ApartmentResponse> findAllApartmentsFromBuilding(UUID buildingId) throws BuildingNotFoundException;
}
