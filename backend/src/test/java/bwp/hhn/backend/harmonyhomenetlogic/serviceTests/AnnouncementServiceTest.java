package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.AnnouncementRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.AnnouncementApartmentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.AnnouncementServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.AnnouncementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApartmentsRepository apartmentsRepository;

    @Mock
    private AnnouncementApartmentRepository announcementApartmentRepository;

    @InjectMocks
    private AnnouncementServiceImp announcementService;

    private User user;
    private Announcement announcement;
    private AnnouncementRequest announcementRequest;
    private UUID userId;
    private Long announcementId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        announcementId = 1L;

        user = User.builder()
                .uuidID(userId)
                .firstName("John")
                .lastName("Doe")
                .announcements(new ArrayList<>())
                .build();

        announcement = Announcement.builder()
                .id(announcementId)
                .title("Test Announcement")
                .content("Test Content")
                .user(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .announcementApartments(new ArrayList<>())
                .build();

        announcementRequest = AnnouncementRequest.builder()
                .userId(userId)
                .title("Updated Announcement")
                .content("Updated Content")
                .build();
    }

    @Test
    void testCreateAnnouncement_Success() throws UserNotFoundException {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> {
            Announcement ann = invocation.getArgument(0);
            ann.setId(announcementId);
            return ann;
        });

        // When
        AnnouncementResponse response = announcementService.createAnnouncement(announcementRequest);

        // Then
        assertNotNull(response);
        assertEquals("Updated Announcement", response.title());
        assertEquals("Updated Content", response.content());
        verify(userRepository, times(1)).findById(userId);
        verify(announcementRepository, times(1)).save(any(Announcement.class));
    }

    @Test
    void testCreateAnnouncement_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> announcementService.createAnnouncement(announcementRequest));
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(announcementRepository);
    }

    @Test
    void testDeleteAnnouncement_Success() throws AnnouncementNotFoundException {
        // Given
        when(announcementRepository.existsById(announcementId)).thenReturn(true);
        doNothing().when(announcementRepository).deleteById(announcementId);

        // When
        String result = announcementService.deleteAnnouncement(announcementId);

        // Then
        assertEquals("Announcement: " + announcementId + " deleted", result);
        verify(announcementRepository, times(1)).existsById(announcementId);
        verify(announcementRepository, times(1)).deleteById(announcementId);
    }

    @Test
    void testDeleteAnnouncement_NotFound() {
        // Given
        when(announcementRepository.existsById(announcementId)).thenReturn(false);

        // When & Then
        assertThrows(AnnouncementNotFoundException.class, () -> announcementService.deleteAnnouncement(announcementId));
        verify(announcementRepository, times(1)).existsById(announcementId);
        verify(announcementRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateAnnouncement_Success() throws AnnouncementNotFoundException, UserNotFoundException {
        // Given
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AnnouncementResponse response = announcementService.updateAnnouncement(announcementId, announcementRequest);

        // Then
        assertNotNull(response);
        assertEquals("Updated Announcement", response.title());
        assertEquals("Updated Content", response.content());
        verify(announcementRepository, times(1)).findById(announcementId);
        verify(announcementRepository, times(1)).save(any(Announcement.class));
    }

    @Test
    void testUpdateAnnouncement_NotFound() {
        // Given
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AnnouncementNotFoundException.class, () -> announcementService.updateAnnouncement(announcementId, announcementRequest));
        verify(announcementRepository, times(1)).findById(announcementId);
        verifyNoMoreInteractions(announcementRepository);
    }

    @Test
    void testGetAnnouncement_Success() throws AnnouncementNotFoundException {
        // Given
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));

        // When
        AnnouncementResponse response = announcementService.getAnnouncement(announcementId);

        // Then
        assertNotNull(response);
        assertEquals("Test Announcement", response.title());
        verify(announcementRepository, times(1)).findById(announcementId);
    }

    @Test
    void testGetAnnouncement_NotFound() {
        // Given
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AnnouncementNotFoundException.class, () -> announcementService.getAnnouncement(announcementId));
        verify(announcementRepository, times(1)).findById(announcementId);
    }

//    @Test
//    void testGetAllAnnouncements() {
//        // Given
//        when(announcementRepository.findAll()).thenReturn(Collections.singletonList(announcement));
//
//        // When
//        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Announcement", responses.get(0).title());
//        verify(announcementRepository, times(1)).findAll();
//    }

//    @Test
//    void testGetAnnouncementsByUserId_Success() throws UserNotFoundException {
//        // Given
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(announcementRepository.findByUserUuidID(userId, pageable)).thenReturn(Collections.singletonList(announcement));
//
//        // When
//        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByUserId(userId);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Announcement", responses.get(0).title());
//        verify(userRepository, times(1)).findById(userId);
//        verify(announcementRepository, times(1)).findByUserUuidID(userId, pageable);
//    }

//    @Test
//    void testGetAnnouncementsByUserId_UserNotFound() {
//        // Given
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(UserNotFoundException.class, () -> announcementService.getAnnouncementsByUserId(userId));
//        verify(userRepository, times(1)).findById(userId);
//        verifyNoInteractions(announcementRepository);
//    }

//    @Test
//    void testGetAnnouncementsFromStartDateToEndDate() {
//        // Given
//        DateRequest dateRequest = new DateRequest();
//        dateRequest.setStartDate(LocalDateTime.now().minusDays(1));
//        dateRequest.setEndDate(LocalDateTime.now().plusDays(1));
//
//        when(announcementRepository.findDistinctByCreatedAtOrUpdatedAtBetween(dateRequest.getStartDate(), dateRequest.getEndDate(), pageable))
//                .thenReturn(Collections.singletonList(announcement));
//
//        // When
//        List<AnnouncementResponse> responses = announcementService.getAnnouncementsFromStartDateTOEndDate(dateRequest);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Announcement", responses.get(0).title());
//        verify(announcementRepository, times(1)).findDistinctByCreatedAtOrUpdatedAtBetween(dateRequest.getStartDate(), dateRequest.getEndDate(), pageable);
//    }

    @Test
    void testLinkAnnouncementsToApartments_Success() throws AnnouncementNotFoundException, ApartmentNotFoundException {
        // Given
        Long announcementId = 1L;
        String apartmentSignature = "A101";
        List<String> apartmentSignatures = Collections.singletonList(apartmentSignature);
        UUID apartmentId = UUID.randomUUID();

        Apartment apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentSignature(apartmentSignature)
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(announcementApartmentRepository.findApartmentIdsByAnnouncementId(announcementId)).thenReturn(Collections.emptyList());
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

        // When
        String result = announcementService.linkAnnouncementsToApartments(announcementId, apartmentSignatures);

        // Then
        assertEquals("Linked 1 apartments to announcement: " + announcementId, result);
        verify(announcementRepository, times(1)).findById(announcementId);
        verify(announcementApartmentRepository, times(1)).findApartmentIdsByAnnouncementId(announcementId);
        verify(apartmentsRepository, times(1)).findByApartmentSignature(apartmentSignature);
        verify(announcementRepository, times(1)).save(any(Announcement.class));
    }

    @Test
    void testLinkAnnouncementsToApartments_AnnouncementNotFound() {
        // Given
        Long announcementId = 1L;
        List<String> apartmentSignatures = Collections.singletonList("A101");

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AnnouncementNotFoundException.class,
                () -> announcementService.linkAnnouncementsToApartments(announcementId, apartmentSignatures));

        verify(announcementRepository, times(1)).findById(announcementId);
        verifyNoMoreInteractions(announcementApartmentRepository);
        verifyNoMoreInteractions(apartmentsRepository);
    }

    @Test
    void testUnlinkAnnouncementsFromApartments_Success() throws AnnouncementNotFoundException {
        // Given
        Long announcementId = 1L;
        String apartmentSignatureToRemove = "A101";
        List<String> apartmentSignaturesToRemove = Collections.singletonList(apartmentSignatureToRemove);

        Apartment apartment1 = Apartment.builder()
                .uuidID(UUID.randomUUID())
                .apartmentSignature(apartmentSignatureToRemove)
                .announcementApartments(new ArrayList<>())
                .build();

        Apartment apartment2 = Apartment.builder()
                .uuidID(UUID.randomUUID())
                .apartmentSignature("A102")
                .announcementApartments(new ArrayList<>())
                .build();

        // Create AnnouncementApartment associations
        AnnouncementApartment announcementApartment1 = AnnouncementApartment.builder()
                .announcement(announcement)
                .apartment(apartment1)
                .build();
        AnnouncementApartment announcementApartment2 = AnnouncementApartment.builder()
                .announcement(announcement)
                .apartment(apartment2)
                .build();

        // Initialize the collections in announcement and apartments
        announcement.setAnnouncementApartments(new ArrayList<>());
        announcement.getAnnouncementApartments().add(announcementApartment1);
        announcement.getAnnouncementApartments().add(announcementApartment2);

        apartment1.getAnnouncementApartments().add(announcementApartment1);
        apartment2.getAnnouncementApartments().add(announcementApartment2);

        // Mock repository methods
        when(announcementRepository.findByIdWithApartments(announcementId)).thenReturn(Optional.of(announcement));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

        // When
        String unlinkResult = announcementService.unlinkAnnouncementsFromApartments(announcementId, apartmentSignaturesToRemove);

        // Then
        assertEquals("Unlinked 1 apartments from announcement: " + announcementId, unlinkResult);
        assertEquals(1, announcement.getAnnouncementApartments().size());
        assertEquals("A102", announcement.getAnnouncementApartments().get(0).getApartment().getApartmentSignature());

        verify(announcementRepository, times(1)).findByIdWithApartments(announcementId);
        verify(announcementRepository, times(1)).save(any(Announcement.class));
    }


    @Test
    void testUnlinkAnnouncementsFromApartments_AnnouncementNotFound() {
        // Given
        Long announcementId = 1L;
        List<String> apartmentSignatures = Collections.singletonList("A101");

        when(announcementRepository.findByIdWithApartments(announcementId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AnnouncementNotFoundException.class,
                () -> announcementService.unlinkAnnouncementsFromApartments(announcementId, apartmentSignatures));

        verify(announcementRepository, times(1)).findByIdWithApartments(announcementId);
        verifyNoMoreInteractions(announcementRepository);
    }

//    @Test
//    void testGetAnnouncementsByApartmentSignature_Success() throws ApartmentNotFoundException {
//        // Given
//        String apartmentSignature = "A101";
//        Apartment apartment = Apartment.builder()
//                .uuidID(UUID.randomUUID())
//                .apartmentSignature(apartmentSignature)
//                .build();
//
//        AnnouncementApartment announcementApartment = AnnouncementApartment.builder()
//                .announcement(announcement)
//                .apartment(apartment)
//                .build();
//
//        when(announcementApartmentRepository.findByApartmentSignature(apartmentSignature, pageable))
//                .thenReturn(Collections.singletonList(announcementApartment));
//
//        // When
//        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByApartmentSignature(apartmentSignature);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Test Announcement", responses.get(0).title());
//        verify(announcementApartmentRepository, times(1)).findByApartmentSignature(apartmentSignature, pageable);
//    }

//    @Test
//    void testGetAnnouncementsByApartmentSignature_NotFound() throws ApartmentNotFoundException {
//        // Given
//        String apartmentSignature = "A101";
//
//        when(announcementApartmentRepository.findByApartmentSignature(apartmentSignature, pageable))
//                .thenReturn(Collections.emptyList());
//
//        // When
//        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByApartmentSignature(apartmentSignature);
//
//        // Then
//        assertNotNull(responses);
//        assertTrue(responses.isEmpty());
//        verify(announcementApartmentRepository, times(1)).findByApartmentSignature(apartmentSignature, pageable);
//    }
}
