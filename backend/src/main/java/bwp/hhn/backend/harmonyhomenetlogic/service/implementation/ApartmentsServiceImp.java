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
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ApartmentsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentsServiceImp implements ApartmentsService {

    private final UserRepository userRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final MailService mailService;
    private final SmsService smsService;

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
    public PageResponse<ApartmentResponse> getAllApartments(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Apartment> apartments = apartmentsRepository.findAll(pageable);

        return getApartmentResponsePageResponse(apartments);
    }

    @Override
    public PossessionHistoryResponse getPossessionHistory(String apartmentSignature, UUID userId)
            throws ApartmentNotFoundException, UserNotFoundException {
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
    public PossessionHistoryResponse createPossessionHistory(String apartmentSignature, UUID userId)
            throws ApartmentNotFoundException, UserNotFoundException {
        // Pobranie użytkownika
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        // Pobranie mieszkania na podstawie sygnatury
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        // Sprawdzenie, czy użytkownik już jest właścicielem tego mieszkania
        if (possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID())) {
            throw new ApartmentNotFoundException("User: " + userId + " already has apartment: " + apartmentSignature);
        }

        // Utworzenie nowego rekordu w historii posiadania
        PossessionHistory possessionHistory = PossessionHistory.builder()
                .user(user)
                .apartment(apartment)
                .build();

        // Zapis rekordu w repozytorium
        PossessionHistory saved = possessionHistoryRepository.save(possessionHistory);

        // Wysyłanie powiadomienia do użytkownika o nowym przypisaniu mieszkania
        if (user.getNotificationTypes() != null) {
            user.getNotificationTypes().forEach(notificationType -> {
                switch (notificationType.getType()) {
                    case EMAIL:
                        mailService.sendNotificationMail(
                                "Aktualizacja własności",
                                "Gratulacje! Zostałeś przypisany jako właściciel mieszkania znajdującego się pod adresem: " + apartment.getAddress(),
                                user.getEmail()
                        );
                        break;
                    case SMS:
                        smsService.sendSms(
                                "Gratulacje! Zostałeś przypisany jako właściciel mieszkania znajdującego się pod adresem: " + apartment.getAddress(),
                                user.getPhoneNumber()
                        );
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + notificationType.getType());
                }
            });
        }

        // Zwracanie odpowiedzi z informacjami o przypisaniu mieszkania
        return PossessionHistoryResponse.builder()
                .userName(saved.getUser().getFirstName() + " " + saved.getUser().getLastName())
                .apartmentName(saved.getApartment().getAddress())
                .startDate(saved.getStartDate())
                .build();
    }

    @Override
    public String deletePossessionHistory(String apartmentSignature, UUID userId) throws PossessionHistoryNotFoundException {
        if (!possessionHistoryRepository.existsByApartmentSignatureAndUserId(apartmentSignature, userId))
            throw new PossessionHistoryNotFoundException("Possession history not found for user: " + userId + " and apartment: " + apartmentSignature);

        possessionHistoryRepository.deleteByApartmentSignatureAndUserId(apartmentSignature, userId);

        return "Possession history deleted successfully";
    }

    @Override
    public PossessionHistoryResponse endPossessionHistory(String apartmentSignature, UUID userId)
            throws ApartmentNotFoundException, UserNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        PossessionHistory possessionHistory = possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID())
                .orElseThrow(() -> new ApartmentNotFoundException("User: " + userId + " not found in apartment: " + apartmentSignature));

        possessionHistory.setEndDate(Instant.now());
        PossessionHistory saved = possessionHistoryRepository.save(possessionHistory);

        return PossessionHistoryResponse.builder()
                .userName(possessionHistory.getUser().getFirstName() + " " + possessionHistory.getUser().getLastName())
                .apartmentName(possessionHistory.getApartment().getAddress())
                .startDate(possessionHistory.getStartDate())
                .endDate(possessionHistory.getEndDate())
                .build();
    }

    @Override
    public List<UserResponse> getCurrentResidents(String apartmentSignature)
            throws ApartmentNotFoundException {

        return possessionHistoryRepository.findActiveResidentsByApartment(apartmentSignature).stream()
                .map(
                        possessionHistory -> UserResponse.builder()
                                .firstName(possessionHistory.getFirstName())
                                .lastName(possessionHistory.getLastName())
                                .email(possessionHistory.getEmail())
                                .build()
                )
                .toList();
    }

    @Override
    public PageResponse<PossessionHistoryResponse> getApartmentPossessionHistory(String apartmentSignature, int pageNo, int pageSize)
            throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<PossessionHistory> possessionHistories = possessionHistoryRepository.findByApartmentUuidID(apartment.getUuidID(), pageable);

        return new PageResponse<>(
                possessionHistories.getNumber(),
                possessionHistories.getSize(),
                possessionHistories.getTotalPages(),
                possessionHistories.getContent().stream()
                        .map(
                                possessionHistory -> PossessionHistoryResponse.builder()
                                        .userName(possessionHistory.getUser().getFirstName() + " " + possessionHistory.getUser().getLastName())
                                        .apartmentName(possessionHistory.getApartment().getAddress())
                                        .startDate(possessionHistory.getStartDate())
                                        .endDate(possessionHistory.getEndDate())
                                        .build()
                        )
                        .toList(),
                possessionHistories.isLast(),
                possessionHistories.hasNext(),
                possessionHistories.hasPrevious()
        );
    }

    @Override
    public List<ApartmentResponse> getAllUserApartments(UUID userId) throws UserNotFoundException {

        return  possessionHistoryRepository.findApartmentsByUserUuidID(userId).stream()
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
                .toList();
    }

    @Override
    public PageResponse<UserResponse> getAllResidentsByApartmentId(String apartmentSignature, int pageNo, int pageSize)
            throws ApartmentNotFoundException {
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment with signature: " + apartmentSignature + " not found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<PossessionHistory> possessionHistories = possessionHistoryRepository.findByApartmentUuidID(apartment.getUuidID(), pageable);

        return new PageResponse<>(
                possessionHistories.getNumber(),
                possessionHistories.getSize(),
                possessionHistories.getTotalPages(),
                possessionHistories.getContent().stream()
                        .map(
                                possessionHistory -> UserResponse.builder()
                                        .firstName(possessionHistory.getUser().getFirstName())
                                        .lastName(possessionHistory.getUser().getLastName())
                                        .email(possessionHistory.getUser().getEmail())
                                        .build()
                        )
                        .toList(),
                possessionHistories.isLast(),
                possessionHistories.hasNext(),
                possessionHistories.hasPrevious()
        );
    }

    private PageResponse<ApartmentResponse> getApartmentResponsePageResponse(Page<Apartment> apartments) {
        return new PageResponse<>(
                apartments.getNumber(),
                apartments.getSize(),
                apartments.getTotalPages(),
                apartments.getContent().stream()
                        .map(
                                apartment -> ApartmentResponse.builder()
                                        .address(apartment.getAddress())
                                        .city(apartment.getCity())
                                        .zipCode(apartment.getZipCode())
                                        .apartmentArea(apartment.getApartmentArea())
                                        .createdAt(apartment.getCreatedAt())
                                        .updatedAt(apartment.getUpdatedAt())
                                        .apartmentSignature(apartment.getApartmentSignature())
                                        .apartmentPercentValue(apartment.getApartmentPercentValue())
                                        .build()
                        )
                        .toList(),
                apartments.isLast(),
                apartments.hasNext(),
                apartments.hasPrevious()
        );
    }
}