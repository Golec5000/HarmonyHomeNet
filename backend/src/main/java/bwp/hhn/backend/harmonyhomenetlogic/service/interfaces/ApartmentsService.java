package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PossessionHistoryNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;

import java.util.List;
import java.util.UUID;

public interface ApartmentsService {

    ApartmentResponse createApartments(ApartmentRequest request);

    ApartmentResponse updateApartment(ApartmentRequest request, String apartmentSignature) throws ApartmentNotFoundException;

    String deleteApartment(String apartmentSignature) throws ApartmentNotFoundException;

    ApartmentResponse getApartmentBySignature(String apartmentSignature) throws ApartmentNotFoundException;

    PageResponse<ApartmentResponse> getCurrentApartmentsByUserId(UUID userId, int pageNo, int pageSize) throws ApartmentNotFoundException, UserNotFoundException;

    PageResponse<ApartmentResponse> getAllApartments(int pageNo, int pageSize);

    PossessionHistoryResponse getPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    PossessionHistoryResponse createPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    String deletePossessionHistory(Long possessionHistoryId) throws PossessionHistoryNotFoundException;

    PossessionHistoryResponse endPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException;

    PageResponse<UserResponse> getCurrentResidents(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

    PageResponse<PossessionHistoryResponse> getApartmentPossessionHistory(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

    List<ApartmentResponse> getAllUserApartments(UUID userId) throws UserNotFoundException;

    PageResponse<UserResponse> getAllResidentsByApartmentId(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

}
