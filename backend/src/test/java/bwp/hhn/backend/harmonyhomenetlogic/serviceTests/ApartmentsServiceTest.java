package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PossessionHistoryNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.ApartmentsServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PossessionHistoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApartmentsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PossessionHistoryRepository possessionHistoryRepository;

    @Mock
    private ApartmentsRepository apartmentsRepository;

    @InjectMocks
    private ApartmentsServiceImp apartmentsService;

    private Apartment apartment;
    private User user;
    private PossessionHistory possessionHistory;
    private UUID apartmentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        apartmentId = UUID.randomUUID();
        userId = UUID.randomUUID();

        apartment = Apartment.builder()
                .uuidID(apartmentId)
                .address("Test Address")
                .city("Test City")
                .zipCode("00-000")
                .apartmentArea(new BigDecimal("50.0"))
                .apartmentPercentValue(new BigDecimal("0.05"))
                .apartmentSignature("A101")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        user = User.builder()
                .uuidID(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        possessionHistory = PossessionHistory.builder()
                .id(1L)
                .user(user)
                .apartment(apartment)
                .startDate(Instant.now().minus(1, ChronoUnit.MONTHS))
                .endDate(null)
                .build();
    }

    @Test
    void testCreateApartment_Success() {
        // Given
        ApartmentRequest request = new ApartmentRequest();
        request.setAddress("New Address");
        request.setCity("New City");
        request.setZipCode("11-111");
        request.setApartmentArea(new BigDecimal("60.0"));
        request.setApartmentPercentValue(new BigDecimal("0.06"));
        request.setApartmentSignature("A102");

        when(apartmentsRepository.save(any(Apartment.class))).thenAnswer(invocation -> {
            Apartment savedApartment = invocation.getArgument(0);
            savedApartment.setUuidID(UUID.randomUUID());
            savedApartment.setCreatedAt(Instant.now());
            savedApartment.setUpdatedAt(Instant.now());
            return savedApartment;
        });

        // When
        ApartmentResponse response = apartmentsService.createApartments(request);

        // Then
        assertNotNull(response);
        assertEquals("New Address", response.address());
        assertEquals("A102", response.apartmentSignature());
        verify(apartmentsRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    void testUpdateApartment_Success() throws ApartmentNotFoundException {
        // Given
        ApartmentRequest request = new ApartmentRequest();
        request.setCity("Updated City");

        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(apartmentsRepository.save(any(Apartment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ApartmentResponse response = apartmentsService.updateApartment(request, "A101");

        // Then
        assertNotNull(response);
        assertEquals("Updated City", response.city());
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(apartmentsRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    void testUpdateApartment_NotFound() {
        // Given
        ApartmentRequest request = new ApartmentRequest();
        request.setCity("Updated City");

        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> apartmentsService.updateApartment(request, "A101"));
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(apartmentsRepository, never()).save(any(Apartment.class));
    }

    @Test
    void testDeleteApartment_Success() throws ApartmentNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        doNothing().when(apartmentsRepository).delete(any(Apartment.class));

        // When
        String result = apartmentsService.deleteApartment("A101");

        // Then
        assertEquals("Apartment deleted successfully", result);
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(apartmentsRepository, times(1)).delete(any(Apartment.class));
    }

    @Test
    void testDeleteApartment_NotFound() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> apartmentsService.deleteApartment("A101"));
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(apartmentsRepository, never()).delete(any(Apartment.class));
    }

    @Test
    void testGetApartmentBySignature_Success() throws ApartmentNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));

        // When
        ApartmentResponse response = apartmentsService.getApartmentBySignature("A101");

        // Then
        assertNotNull(response);
        assertEquals("Test Address", response.address());
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
    }

    @Test
    void testGetApartmentBySignature_NotFound() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> apartmentsService.getApartmentBySignature("A101"));
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
    }

//    @Test
//    void testGetCurrentApartmentsByUserId_Success() throws ApartmentNotFoundException, UserNotFoundException {
//        // Given
//        when(possessionHistoryRepository.findByUserUuidIDAndEndDateIsNull(userId, pageable))
//                .thenReturn(Collections.singletonList(possessionHistory));
//
//        // When
//        List<ApartmentResponse> responses = apartmentsService.getCurrentApartmentsByUserId(userId);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Address", responses.get(0).address());
//        verify(possessionHistoryRepository, times(1)).findByUserUuidIDAndEndDateIsNull(userId, pageable);
//    }

    @Test
    void testGetPossessionHistory_Success() throws ApartmentNotFoundException, UserNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartmentId))
                .thenReturn(Optional.of(possessionHistory));

        // When
        PossessionHistoryResponse response = apartmentsService.getPossessionHistory("A101", userId);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.userName());
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).findByUserUuidIDAndApartmentUuidID(userId, apartmentId);
    }

    @Test
    void testGetPossessionHistory_NotFound() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartmentId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> apartmentsService.getPossessionHistory("A101", userId));
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).findByUserUuidIDAndApartmentUuidID(userId, apartmentId);
    }

    @Test
    void testCreatePossessionHistory_Success() throws ApartmentNotFoundException, UserNotFoundException {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartmentId)).thenReturn(false);
        when(possessionHistoryRepository.save(any(PossessionHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PossessionHistoryResponse response = apartmentsService.createPossessionHistory("A101", userId);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.userName());
        verify(userRepository, times(1)).findById(userId);
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(userId, apartmentId);
        verify(possessionHistoryRepository, times(1)).save(any(PossessionHistory.class));
    }

    @Test
    void testCreatePossessionHistory_UserAlreadyHasApartment() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartmentId)).thenReturn(true);

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> apartmentsService.createPossessionHistory("A101", userId));
        verify(userRepository, times(1)).findById(userId);
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(userId, apartmentId);
        verify(possessionHistoryRepository, never()).save(any(PossessionHistory.class));
    }

    @Test
    void testEndPossessionHistory_Success() throws ApartmentNotFoundException, UserNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.findByUserUuidIDAndApartmentUuidID(userId, apartmentId))
                .thenReturn(Optional.of(possessionHistory));
        when(possessionHistoryRepository.save(any(PossessionHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PossessionHistoryResponse response = apartmentsService.endPossessionHistory("A101", userId);

        // Then
        assertNotNull(response);
        assertNotNull(response.endDate());
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).findByUserUuidIDAndApartmentUuidID(userId, apartmentId);
        verify(possessionHistoryRepository, times(1)).save(any(PossessionHistory.class));
    }

    @Test
    void testDeletePossessionHistory_Success() throws PossessionHistoryNotFoundException {
        // Given
        Long possessionHistoryId = 1L;
        when(possessionHistoryRepository.existsById(possessionHistoryId)).thenReturn(true);
        doNothing().when(possessionHistoryRepository).deleteById(possessionHistoryId);

        // When
        String result = apartmentsService.deletePossessionHistory(possessionHistoryId);

        // Then
        assertEquals("Possession history deleted successfully", result);
        verify(possessionHistoryRepository, times(1)).existsById(possessionHistoryId);
        verify(possessionHistoryRepository, times(1)).deleteById(possessionHistoryId);
    }

    @Test
    void testDeletePossessionHistory_NotFound() {
        // Given
        Long possessionHistoryId = 1L;
        when(possessionHistoryRepository.existsById(possessionHistoryId)).thenReturn(false);

        // When & Then
        assertThrows(PossessionHistoryNotFoundException.class, () -> apartmentsService.deletePossessionHistory(possessionHistoryId));
        verify(possessionHistoryRepository, times(1)).existsById(possessionHistoryId);
        verify(possessionHistoryRepository, never()).deleteById(possessionHistoryId);
    }

//    @Test
//    void testGetCurrentResidents_Success() throws ApartmentNotFoundException {
//        // Given
//        when(possessionHistoryRepository.findActiveResidentsByApartment("A101", pageable)).thenReturn(Collections.singletonList(user));
//
//        // When
//        List<UserResponse> responses = apartmentsService.getCurrentResidents("A101");
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("John", responses.get(0).firstName());
//        verify(possessionHistoryRepository, times(1)).findActiveResidentsByApartment("A101", pageable);
//    }

//    @Test
//    void testGetApartmentPossessionHistory_Success() throws ApartmentNotFoundException {
//        // Given
//        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
//        when(possessionHistoryRepository.findByApartmentUuidID(apartmentId, pageable)).thenReturn(Collections.singletonList(possessionHistory));
//
//        // When
//        List<PossessionHistoryResponse> responses = apartmentsService.getApartmentPossessionHistory("A101");
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("John Doe", responses.get(0).userName());
//        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
//        verify(possessionHistoryRepository, times(1)).findByApartmentUuidID(apartmentId, pageable);
//    }

//    @Test
//    void testGetAllUserApartments_Success() throws UserNotFoundException {
//        // Given
//        when(possessionHistoryRepository.findByUserUuidID(userId, pageable)).thenReturn(Collections.singletonList(possessionHistory));
//
//        // When
//        List<ApartmentResponse> responses = apartmentsService.getAllUserApartments(userId);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Address", responses.get(0).address());
//        verify(possessionHistoryRepository, times(1)).findByUserUuidID(userId, pageable);
//    }

//    @Test
//    void testGetAllResidentsByApartmentId_Success() throws ApartmentNotFoundException {
//        // Given
//        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
//        when(possessionHistoryRepository.findByApartmentUuidID(apartmentId, pageable)).thenReturn(Collections.singletonList(possessionHistory));
//
//        // When
//        List<UserResponse> responses = apartmentsService.getAllResidentsByApartmentId("A101");
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("John", responses.get(0).firstName());
//        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
//        verify(possessionHistoryRepository, times(1)).findByApartmentUuidID(apartmentId, pageable);
//    }

}
