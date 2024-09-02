package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ParkingSpaceNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.repository.ParkingSpaceRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImp implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;


    @Override
    public void deleteById(UUID id) throws ParkingSpaceNotFoundException {
        if (parkingSpaceRepository.existsById(id)) parkingSpaceRepository.deleteById(id);
        else throw new ParkingSpaceNotFoundException("wrong parking space id");

    }

    @Override
    public ParkingSpace findById(UUID id) throws ParkingSpaceNotFoundException {
        return parkingSpaceRepository.findById(id)
                .orElseThrow(() -> new ParkingSpaceNotFoundException("wrong parking space id"));
    }

    @Override
    public List<ParkingSpace> findAll() {
        return parkingSpaceRepository.findAll();
    }

    @Override
    public ParkingSpaceResponse save(@NonNull ParkingSpace parkingSpace) {
        parkingSpaceRepository.save(parkingSpace);
        return mapParkingSpaceToParkingSpaceResponse(parkingSpace);
    }

    @Override
    public ParkingSpaceResponse mapParkingSpaceToParkingSpaceResponse(@NonNull ParkingSpace parkingSpace) {
        return ParkingSpaceResponse.builder()
                .id(parkingSpace.getId())
                .number(parkingSpace.getNumber())
                .build();
    }

    @Override
    public List<ParkingSpaceResponse> mapParkingSpaceListToParkingSpaceResponseList(@NonNull List<ParkingSpace> parkingSpaceList) {
        return parkingSpaceList.stream()
                .map(this::mapParkingSpaceToParkingSpaceResponse)
                .toList();
    }
}
