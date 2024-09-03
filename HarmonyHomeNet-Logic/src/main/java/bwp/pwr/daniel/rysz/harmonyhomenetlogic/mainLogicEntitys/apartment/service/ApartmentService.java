package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ApartmentService {

    List<Apartment> findAll();

    Apartment findById(UUID id) throws ApartmentNotFoundException;

    ApartmentResponse save(@NonNull Apartment newApartment);

    void deleteById(UUID id) throws ApartmentNotFoundException;

    Apartment findByApartmentNumber(int apartmentNumber, UUID buildingId) throws ApartmentNotFoundException;

    UserResponse addResidentTenantToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException;

    UserResponse addResidentOwnerToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException;

    ApartmentResponse mapApartmentToApartmentResponse(Apartment apartment);

    List<ApartmentResponse> mapApartmentListToApartmentResponseList(List<Apartment> apartmentList);
}
