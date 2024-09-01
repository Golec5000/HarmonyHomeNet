package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repositorys.ApartmentRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.repository.BasementRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service.BasementService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.repository.BuildingRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.repository.ParkingSpaceRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.service.ParkingSpaceService;
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
    public List<BuildingResponse> findAll() {
        return buildingRepository.findAll().stream()
                .map(building -> BuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getBuildingName())
                        .region(building.getRegion())
                        .city(building.getCity())
                        .street(building.getStreet())
                        .build()
                ).toList();
    }

    @Override
    public BuildingResponse findById(UUID id) throws BuildingNotFoundException {
        return buildingRepository.findById(id)
                .map(building -> BuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getBuildingName())
                        .region(building.getRegion())
                        .city(building.getCity())
                        .street(building.getStreet())
                        .build()
                ).orElseThrow(() -> new BuildingNotFoundException("wrong building id"));
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
    public BuildingResponse findByBuildingName(@NonNull String name) throws BuildingNotFoundException {
        return buildingRepository.findByBuildingName(name)
                .map(building -> BuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getBuildingName())
                        .region(building.getRegion())
                        .city(building.getCity())
                        .street(building.getStreet())
                        .build()
                ).orElseThrow(() -> new BuildingNotFoundException("wrong building name"));
    }

    @Override
    public List<BuildingResponse> findAllByRegion(@NonNull String region) {
        return buildingRepository.findAllByRegion(region).stream()
                .map(building -> BuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getBuildingName())
                        .region(building.getRegion())
                        .city(building.getCity())
                        .street(building.getStreet())
                        .build()
                ).toList();
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

        ApartmentResponse apartmentResponse= apartmentService.save(newApartment);
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

        //@todo add basementService.save
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

        //@todo add parkingSpaceService.save
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
}
