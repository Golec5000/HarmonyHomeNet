package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BasementNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ParkingSpaceNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
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
    List<Building> findAll();

    Building findById(UUID id) throws BuildingNotFoundException;

    BuildingResponse save(@NonNull Building newBuilding);

    void deleteById(UUID id) throws BuildingNotFoundException;

    Building findByBuildingName(@NonNull String name) throws BuildingNotFoundException;

    List<Building> findAllByRegion(@NonNull Region region);

    ApartmentResponse addApartmentToBuilding(UUID buildingId, @NonNull ApartmentRequest apartmentRequest) throws BuildingNotFoundException;

    BasementResponse addBasementToBuilding(UUID buildingId, @NonNull BasementRequest basementRequest) throws BuildingNotFoundException;

    ParkingSpaceResponse addParkingSpaceToBuilding(UUID buildingId, @NonNull ParkingSpaceRequest parkingSpaceRequest) throws BuildingNotFoundException;

    List<ApartmentResponse> findAllApartmentsFromBuilding(UUID buildingId) throws BuildingNotFoundException;

    void deleteApartmentFromBuilding(UUID buildingId, UUID apartmentId) throws BuildingNotFoundException, ApartmentNotFoundException;

    void deleteBasementFromBuilding(UUID buildingId, UUID basementId) throws BuildingNotFoundException, BasementNotFoundException;

    void deleteParkingSpaceFromBuilding(UUID buildingId, UUID parkingSpaceId) throws BuildingNotFoundException, ParkingSpaceNotFoundException;

    BuildingResponse mapBuildingToBuildingResponse(@NonNull Building building);

    List<BuildingResponse> mapBuildingListToBuildingResponseList(@NonNull List<Building> buildingList);
}
