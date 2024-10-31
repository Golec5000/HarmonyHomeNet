package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PossessionHistoryNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface ApartmentsService {

    ApartmentResponse createApartments(ApartmentRequest request);

    ApartmentResponse updateApartment(ApartmentRequest request, String apartmentSignature) throws ApartmentNotFoundException;

    String deleteApartment(String apartmentSignature) throws ApartmentNotFoundException;

    ApartmentResponse getApartmentBySignature(String apartmentSignature) throws ApartmentNotFoundException;

    List<ApartmentResponse> getCurrentApartmentsByUserId(UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    List<ApartmentResponse> getAllApartments();

    PossessionHistoryResponse getPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    PossessionHistoryResponse createPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    String deletePossessionHistory(Long possessionHistoryId) throws PossessionHistoryNotFoundException;

    PossessionHistoryResponse endPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    List<UserResponse> getCurrentResidents(String apartmentSignature) throws ApartmentNotFoundException;

    List<PossessionHistoryResponse> getApartmentPossessionHistory(String apartmentSignature) throws ApartmentNotFoundException;

    List<ApartmentResponse> getAllUserApartments(UUID userId) throws UserNotFoundException;

    List<UserResponse> getAllResidentsByApartmentId(String apartmentSignature) throws ApartmentNotFoundException;

}
