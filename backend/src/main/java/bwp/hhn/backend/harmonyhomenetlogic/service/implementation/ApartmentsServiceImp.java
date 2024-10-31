package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PossessionHistoryNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ApartmentsService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentsServiceImp implements ApartmentsService {

    private final UserRepository userRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final ApartmentsRepository apartmentsRepository;

    @Override
    public ApartmentResponse createApartments(ApartmentRequest request) {
        Apartment apartment = Apartment.builder()
                .address(request.getAddress())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .apartmentArea(request.getApartmentArea())
                .apartmentPercentValue(request.getApartmentPercentValue())
                .apartmentSignature(request.getApartmentSignature())
                .build();

        Apartment saved = apartmentsRepository.save(apartment);

        return ApartmentResponse.builder()
                .apartmentId(saved.getUuidID())
                .address(saved.getAddress())
                .city(saved.getCity())
                .zipCode(saved.getZipCode())
                .apartmentArea(saved.getApartmentArea())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .apartmentSignature(saved.getApartmentSignature())
                .build();
    }

    @Override
    public ApartmentResponse updateApartment(ApartmentRequest request, String apartmentSignature) throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        apartment.setAddress(request.getAddress() != null ? request.getAddress() : apartment.getAddress());
        apartment.setCity(request.getCity() != null ? request.getCity() : apartment.getCity());
        apartment.setZipCode(request.getZipCode() != null ? request.getZipCode() : apartment.getZipCode());
        apartment.setApartmentArea(request.getApartmentArea() != null ? request.getApartmentArea() : apartment.getApartmentArea());
        apartment.setApartmentPercentValue(request.getApartmentPercentValue() != null ? request.getApartmentPercentValue() : apartment.getApartmentPercentValue());

        Apartment saved = apartmentsRepository.save(apartment);

        return ApartmentResponse.builder()
                .address(saved.getAddress())
                .city(saved.getCity())
                .zipCode(saved.getZipCode())
                .apartmentArea(saved.getApartmentArea())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .apartmentSignature(saved.getApartmentSignature())
                .build();
    }

    @Override
    public String deleteApartment(String apartmentSignature) throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        apartmentsRepository.delete(apartment);

        return "Apartment deleted successfully";
    }

    @Override
    public ApartmentResponse getApartmentBySignature(String apartmentSignature) throws ApartmentNotFoundException {
        return apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .map(
                        apartment -> ApartmentResponse.builder()
                                .address(apartment.getAddress())
                                .city(apartment.getCity())
                                .zipCode(apartment.getZipCode())
                                .apartmentArea(apartment.getApartmentArea())
                                .createdAt(apartment.getCreatedAt())
                                .updatedAt(apartment.getUpdatedAt())
                                .apartmentSignature(apartment.getApartmentSignature())
                                .build()
                )
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));
    }

    @Override
    public List<ApartmentResponse> getCurrentApartmentsByUserId(UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return possessionHistoryRepository.findByUserUuidIDAndEndDateIsNull(userId).stream()
                .map(
                        possessionHistory -> ApartmentResponse.builder()
                                .address(possessionHistory.getApartment().getAddress())
                                .city(possessionHistory.getApartment().getCity())
                                .zipCode(possessionHistory.getApartment().getZipCode())
                                .apartmentArea(possessionHistory.getApartment().getApartmentArea())
                                .createdAt(possessionHistory.getApartment().getCreatedAt())
                                .updatedAt(possessionHistory.getApartment().getUpdatedAt())
                                .apartmentSignature(possessionHistory.getApartment().getApartmentSignature())
                                .build()
                )
                .toList();
    }

    @Override
    public List<ApartmentResponse> getAllApartments() {
        return apartmentsRepository.findAll().stream()
                .map(
                        apartment -> ApartmentResponse.builder()
                                .apartmentId(apartment.getUuidID())
                                .address(apartment.getAddress())
                                .city(apartment.getCity())
                                .zipCode(apartment.getZipCode())
                                .apartmentArea(apartment.getApartmentArea())
                                .createdAt(apartment.getCreatedAt())
                                .updatedAt(apartment.getUpdatedAt())
                                .apartmentSignature(apartment.getApartmentSignature())
                                .build()
                )
                .toList();
    }

    @Override
    public PossessionHistoryResponse getPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        return possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID())
                .map(
                        possessionHistory -> PossessionHistoryResponse.builder()
                                .userName(possessionHistory.getUser().getFirstName() + " " + possessionHistory.getUser().getLastName())
                                .apartmentName(possessionHistory.getApartment().getAddress())
                                .startDate(possessionHistory.getStartDate())
                                .endDate(possessionHistory.getEndDate())
                                .build()
                )
                .orElseThrow(() -> new ApartmentNotFoundException("Possession history not found for user: " + userId + " and apartment: " + apartmentSignature));
    }

    @Override
    @Transactional
    public PossessionHistoryResponse createPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        if (possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID()))
            throw new ApartmentNotFoundException("User: " + userId + " already has apartment: " + apartmentSignature);

        PossessionHistory possessionHistory = PossessionHistory.builder()
                .user(user)
                .apartment(apartment)
                .build();

        PossessionHistory saved = possessionHistoryRepository.save(possessionHistory);

        return PossessionHistoryResponse.builder()
                .userName(saved.getUser().getFirstName() + " " + saved.getUser().getLastName())
                .apartmentName(saved.getApartment().getAddress())
                .startDate(saved.getStartDate())
                .build();
    }

    @Override
    public String deletePossessionHistory(Long possessionHistoryId) throws PossessionHistoryNotFoundException {
        if (!possessionHistoryRepository.existsById(possessionHistoryId))
            throw new PossessionHistoryNotFoundException("Possession history: " + possessionHistoryId + " not found");

        possessionHistoryRepository.deleteById(possessionHistoryId);

        return "Possession history deleted successfully";
    }

    @Override
    public PossessionHistoryResponse endPossessionHistory(String apartmentSignature, UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        PossessionHistory possessionHistory = possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID())
                .orElseThrow(() -> new ApartmentNotFoundException("User: " + userId + " not found in apartment: " + apartmentSignature));

        possessionHistory.setEndDate(LocalDateTime.now());
        PossessionHistory saved = possessionHistoryRepository.save(possessionHistory);

        return PossessionHistoryResponse.builder()
                .userName(possessionHistory.getUser().getFirstName() + " " + possessionHistory.getUser().getLastName())
                .apartmentName(possessionHistory.getApartment().getAddress())
                .startDate(possessionHistory.getStartDate())
                .endDate(possessionHistory.getEndDate())
                .build();
    }

    @Override
    public List<UserResponse> getCurrentResidents(String apartmentSignature) throws ApartmentNotFoundException {

        return possessionHistoryRepository.findActiveResidentsByApartment(apartmentSignature).stream()
                .map(
                        user -> UserResponse.builder()
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .build()
                )
                .toList();
    }

    @Override
    public List<PossessionHistoryResponse> getApartmentPossessionHistory(String apartmentSignature) throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        return possessionHistoryRepository.findByApartmentUuidID(apartment.getUuidID()).stream()
                .map(
                        possessionHistory -> PossessionHistoryResponse.builder()
                                .userName(possessionHistory.getUser().getFirstName() + " " + possessionHistory.getUser().getLastName())
                                .apartmentName(possessionHistory.getApartment().getAddress())
                                .startDate(possessionHistory.getStartDate())
                                .endDate(possessionHistory.getEndDate())
                                .build()
                )
                .toList();
    }

    @Override
    public List<ApartmentResponse> getAllUserApartments(UUID userId) throws UserNotFoundException {
        return possessionHistoryRepository.findByUserUuidID(userId).stream()
                .map(
                        possessionHistory -> ApartmentResponse.builder()
                                .address(possessionHistory.getApartment().getAddress())
                                .city(possessionHistory.getApartment().getCity())
                                .zipCode(possessionHistory.getApartment().getZipCode())
                                .apartmentArea(possessionHistory.getApartment().getApartmentArea())
                                .createdAt(possessionHistory.getApartment().getCreatedAt())
                                .updatedAt(possessionHistory.getApartment().getUpdatedAt())
                                .apartmentSignature(possessionHistory.getApartment().getApartmentSignature())
                                .build()
                )
                .toList();
    }

    @Override
    public List<UserResponse> getAllResidentsByApartmentId(String apartmentSignature) throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        return possessionHistoryRepository.findByApartmentUuidID(apartment.getUuidID()).stream()
                .map(
                        possessionHistory -> UserResponse.builder()
                                .firstName(possessionHistory.getUser().getFirstName())
                                .lastName(possessionHistory.getUser().getLastName())
                                .email(possessionHistory.getUser().getEmail())
                                .build()
                )
                .toList();
    }
}