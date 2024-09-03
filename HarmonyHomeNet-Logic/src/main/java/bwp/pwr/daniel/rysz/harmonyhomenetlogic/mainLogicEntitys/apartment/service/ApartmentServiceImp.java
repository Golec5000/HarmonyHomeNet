package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity.ApartmentResidentAssignment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repository.ApartmentRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repository.ApartmentResidentAssignmentRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service.UserService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.ResourceRole;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentServiceImp implements ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentResidentAssignmentRepository apartmentResidentAssignmentRepository;
    private final UserService userService;

    @Override
    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }

    @Override
    public Apartment findById(UUID id) throws ApartmentNotFoundException {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));
    }

    @Override
    public ApartmentResponse save(@NonNull Apartment newApartment) {

        apartmentRepository.save(newApartment);

        return mapApartmentToApartmentResponse(newApartment);
    }

    @Override
    public void deleteById(UUID id) throws ApartmentNotFoundException {

        if (apartmentRepository.existsById(id)) apartmentRepository.deleteById(id);
        else throw new ApartmentNotFoundException("wrong apartment id");

    }

    @Override
    public Apartment findByApartmentNumber(int apartmentNumber, UUID buildingId) throws ApartmentNotFoundException {
        return apartmentRepository.findByApartmentNumberAndBuildingId(apartmentNumber, buildingId)
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment number"));
    }

    @Override
    @Transactional
    public UserResponse addResidentTenantToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException {
        Resident resident = (Resident) userService.findById(userId);

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));

        if (apartmentResidentAssignmentRepository.existsByApartmentIdAndResidentId(apartmentId, userId)) {
            throw new IllegalArgumentException("User is already assigned to this apartment");
        }

        return addResidentsToApartment(resident, apartment, ResourceRole.APARTMENT_TENANT);
    }

    @Override
    @Transactional
    public UserResponse addResidentOwnerToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException {
        Resident resident = (Resident) userService.findById(userId);

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));

        if (apartmentResidentAssignmentRepository.existsByApartmentIdAndResidentId(apartmentId, userId)) {
            throw new IllegalArgumentException("User is already assigned to this apartment");
        }

        return addResidentsToApartment(resident, apartment, ResourceRole.APARTMENT_OWNER);
    }

    @Override
    public ApartmentResponse mapApartmentToApartmentResponse(Apartment apartment) {
        return ApartmentResponse.builder()
                .id(apartment.getId())
                .apartmentNumber(apartment.getApartmentNumber())
                .area(apartment.getArea())
                .build();
    }

    @Override
    public List<ApartmentResponse> mapApartmentListToApartmentResponseList(List<Apartment> apartmentList) {
        return apartmentList.stream()
                .map(this::mapApartmentToApartmentResponse)
                .toList();
    }

    private UserResponse addResidentsToApartment(Resident resident, Apartment apartment, ResourceRole role) {

        ApartmentResidentAssignment assignment = ApartmentResidentAssignment.builder()
                .resident(resident)
                .apartment(apartment)
                .resourceRole(role)
                .build();

        apartmentResidentAssignmentRepository.save(assignment);
        if (apartment.getApartmentAssignments() == null) apartment.setApartmentAssignments(new ArrayList<>());
        apartment.getApartmentAssignments().add(assignment);

        if (resident.getApartmentResidentAssignments() == null) resident.setApartmentResidentAssignments(new ArrayList<>());
        resident.getApartmentResidentAssignments().add(assignment);

        UserResponse userResponse = userService.save(resident);
        apartmentRepository.save(apartment);

        return userResponse;
    }
}
