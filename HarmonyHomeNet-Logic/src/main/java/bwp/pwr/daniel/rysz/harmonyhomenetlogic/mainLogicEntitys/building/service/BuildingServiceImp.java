package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BasementNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ParkingSpaceNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.service.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository.BuildingRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service.ParkingSpaceService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ApartmentRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.BasementRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff.ParkingSpaceRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BuildingResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ParkingSpaceResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingServiceImp implements BuildingService {

    private final BuildingRepository buildingRepository;

    private final ApartmentService apartmentService;
    private final BasementService basementService;
    private final ParkingSpaceService parkingSpaceService;


    @Override
    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    @Override
    public Building findById(UUID id) throws BuildingNotFoundException {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));
    }

    @Override
    public BuildingResponse save(@NonNull Building newBuilding) {

        buildingRepository.save(newBuilding);

        return BuildingResponse.builder()
                .id(newBuilding.getId())
                .buildingName(newBuilding.getBuildingName())
                .region(newBuilding.getRegion())
                .city(newBuilding.getCity())
                .street(newBuilding.getStreet())
                .build();
    }

    @Override
    public void deleteById(UUID id) throws BuildingNotFoundException {

        if (buildingRepository.existsById(id)) buildingRepository.deleteById(id);
        else throw new BuildingNotFoundException("wrong building id");

    }

    @Override
    public Building findByBuildingName(@NonNull String name) throws BuildingNotFoundException {
        return buildingRepository.findByBuildingName(name)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building name"));
    }

    @Override
    public List<Building> findAllByRegion(@NonNull Region region) {
        return buildingRepository.findAllByRegion(region);
    }

    @Override
    @Transactional
    public ApartmentResponse addApartmentToBuilding(UUID buildingId, @NonNull ApartmentRequest apartmentRequest) throws BuildingNotFoundException {
        Building buildingToUpdate = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Apartment newApartment = Apartment.builder()
                .apartmentNumber(apartmentRequest.apartmentNumber())
                .area(apartmentRequest.area())
                .building(buildingToUpdate)
                .build();

        if (buildingToUpdate.getApartments() == null) buildingToUpdate.setApartments(new ArrayList<>());
        buildingToUpdate.getApartments().add(newApartment);

        ApartmentResponse apartmentResponse = apartmentService.save(newApartment);
        buildingRepository.save(buildingToUpdate);

        return apartmentResponse;
    }

    @Override
    @Transactional
    public BasementResponse addBasementToBuilding(UUID buildingId, @NonNull BasementRequest basementRequest) throws BuildingNotFoundException {
        Building buildingToUpdate = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Basement newBasement = Basement.builder()
                .basementNumber(basementRequest.basementNumber())
                .area(basementRequest.area())
                .building(buildingToUpdate)
                .build();

        if (buildingToUpdate.getBasements() == null) buildingToUpdate.setBasements(new ArrayList<>());
        buildingToUpdate.getBasements().add(newBasement);

        BasementResponse basementResponse = basementService.save(newBasement);
        buildingRepository.save(buildingToUpdate);

        return basementResponse;
    }

    @Override
    @Transactional
    public ParkingSpaceResponse addParkingSpaceToBuilding(UUID buildingId, @NonNull ParkingSpaceRequest parkingSpaceRequest) throws BuildingNotFoundException {
        Building buildingToUpdate = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        ParkingSpace newParkingSpace = ParkingSpace.builder()
                .number(parkingSpaceRequest.number())
                .building(buildingToUpdate)
                .build();

        if (buildingToUpdate.getParkingSpaces() == null) buildingToUpdate.setParkingSpaces(new ArrayList<>());
        buildingToUpdate.getParkingSpaces().add(newParkingSpace);

        ParkingSpaceResponse parkingSpaceResponse = parkingSpaceService.save(newParkingSpace);
        buildingRepository.save(buildingToUpdate);

        return parkingSpaceResponse;
    }

    @Override
    public List<ApartmentResponse> findAllApartmentsFromBuilding(UUID buildingId) throws BuildingNotFoundException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        return building.getApartments().stream()
                .map(apartment -> ApartmentResponse.builder()
                        .id(apartment.getId())
                        .apartmentNumber(apartment.getApartmentNumber())
                        .area(apartment.getArea())
                        .build()
                ).toList();
    }

    @Override
    @Transactional
    public void deleteApartmentFromBuilding(UUID buildingId, UUID apartmentId) throws BuildingNotFoundException, ApartmentNotFoundException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Apartment apartment = building.getApartments().stream()
                .filter(apartment1 -> apartment1.getId().equals(apartmentId))
                .findFirst()
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));

        building.getApartments().remove(apartment);
        apartmentService.deleteById(apartmentId);
        buildingRepository.save(building);

    }

    @Override
    @Transactional
    public void deleteBasementFromBuilding(UUID buildingId, UUID basementId) throws BuildingNotFoundException, BasementNotFoundException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        Basement basement = building.getBasements().stream()
                .filter(basement1 -> basement1.getId().equals(basementId))
                .findFirst()
                .orElseThrow(() -> new BasementNotFoundException("wrong basement id"));

        building.getBasements().remove(basement);
        basementService.deleteById(basementId);
        buildingRepository.save(building);
    }

    @Override
    @Transactional
    public void deleteParkingSpaceFromBuilding(UUID buildingId, UUID parkingSpaceId) throws BuildingNotFoundException, ParkingSpaceNotFoundException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("wrong building id"));

        ParkingSpace parkingSpace = building.getParkingSpaces().stream()
                .filter(parkingSpace1 -> parkingSpace1.getId().equals(parkingSpaceId))
                .findFirst()
                .orElseThrow(() -> new ParkingSpaceNotFoundException("wrong parking space id"));

        building.getParkingSpaces().remove(parkingSpace);
        parkingSpaceService.deleteById(parkingSpaceId);
        buildingRepository.save(building);

    }

    @Override
    public BuildingResponse mapBuildingToBuildingResponse(@NonNull Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .buildingName(building.getBuildingName())
                .region(building.getRegion())
                .city(building.getCity())
                .street(building.getStreet())
                .build();
    }

    @Override
    public List<BuildingResponse> mapBuildingListToBuildingResponseList(@NonNull List<Building> buildingList) {
        return buildingList.stream()
                .map(building -> BuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getBuildingName())
                        .region(building.getRegion())
                        .city(building.getCity())
                        .street(building.getStreet())
                        .build()
                ).toList();
    }
}
