package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ParkingSpaceNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;

import java.util.List;
import java.util.UUID;

public interface ParkingSpaceService {
    void deleteById(UUID id) throws ParkingSpaceNotFoundException;
    ParkingSpace findById(UUID id) throws ParkingSpaceNotFoundException;
    List<ParkingSpaceResponse> findAll();
    ParkingSpaceResponse save(ParkingSpace parkingSpace);
}
