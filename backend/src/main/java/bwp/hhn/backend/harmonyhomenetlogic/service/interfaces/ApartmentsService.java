package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface ApartmentsService {

    ApartmentResponse createApartments(ApartmentRequest request);

    ApartmentResponse updateApartment(ApartmentRequest request, UUID apartmentId) throws ApartmentNotFoundException;

    String deleteApartment(UUID apartmentId) throws ApartmentNotFoundException;

    ApartmentResponse getApartmentById(UUID apartmentId) throws ApartmentNotFoundException;

    List<ApartmentResponse> getApartmentsByUserId(UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    List<ApartmentResponse> getAllApartments();

    PossessionHistoryResponse getPossessionHistory(UUID apartmentId, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    PossessionHistoryResponse createPossessionHistory(UUID apartmentId, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    PossessionHistoryResponse deletePossessionHistory(UUID apartmentId, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    List<UserResponse> getCurrentResidents(UUID apartmentId) throws ApartmentNotFoundException;

    List<PossessionHistoryResponse> getApartmentPossessionHistory(UUID apartmentId) throws ApartmentNotFoundException;

    List<ApartmentResponse> getAllUserApartments(UUID userId) throws UserNotFoundException;
// @todo do szatanowienia
//    PossessionHistoryResponse updatePossessionHistory(UUID possessionHistoryId, PossessionHistoryRequest request) throws PossessionHistoryNotFoundException;

    List<UserResponse> getAllResidentsByApartmentId(UUID apartmentId) throws ApartmentNotFoundException;

}
