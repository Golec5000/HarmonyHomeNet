package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ApartmentService {
    List<ApartmentResponse> findAll();

    ApartmentResponse findById(UUID id) throws ApartmentNotFoundException;

    ApartmentResponse save(@NonNull Apartment newApartment);

    void deleteById(UUID id) throws ApartmentNotFoundException;

    ApartmentResponse findByApartmentNumber(int apartmentNumber, UUID buildingId) throws ApartmentNotFoundException;

    List<UserResponse> findUserByApartmentId(UUID apartmentId) throws ApartmentNotFoundException;

    UserResponse addResidentTenantToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException;

    UserResponse addResidentOwnerToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException;
}
