package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.AnnouncementRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.AnnouncementApartmentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.AnnouncementServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.AnnouncementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.Instant;
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

    @Mock
    private MailService mailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private AnnouncementServiceImp announcementService;

    private User user;
    private Announcement announcement;
    private AnnouncementRequest announcementRequest;
    private UUID userId;
    private Long announcementId;
    private int pageNo;
    private int pageSize;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        announcementId = 1L;
        pageNo = 0;
        pageSize = 10;
        pageable = PageRequest.of(pageNo, pageSize);

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

    @Test
    void testGetAllAnnouncements() {
        // Given
        List<Announcement> announcementList = Collections.singletonList(announcement);
        Page<Announcement> announcementPage = new PageImpl<>(announcementList, pageable, 1);

        when(announcementRepository.findAll(pageable)).thenReturn(announcementPage);

        // When
        PageResponse<AnnouncementResponse> response = announcementService.getAllAnnouncements(pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalPages());
        assertEquals(1, response.content().size());
        assertEquals("Test Announcement", response.content().get(0).title());

        verify(announcementRepository, times(1)).findAll(pageable);
    }

    @Test
    void testLinkAnnouncementsToApartments_Success() throws AnnouncementNotFoundException, ApartmentNotFoundException {
        // Given
        Long announcementId = 1L;
        String apartmentSignature = "A101";
        List<String> apartmentSignatures = Collections.singletonList(apartmentSignature);
        UUID apartmentId = UUID.randomUUID();

        User resident = User.builder()
                .uuidID(UUID.randomUUID())
                .email("resident@example.com")
                .phoneNumber("123456789")
                .notificationTypes(Arrays.asList(
                        NotificationType.builder().type(Notification.EMAIL).build(),
                        NotificationType.builder().type(Notification.SMS).build()
                ))
                .build();

        PossessionHistory possessionHistory = PossessionHistory.builder()
                .user(resident)
                .build();

        Apartment apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentSignature(apartmentSignature)
                .possessionHistories(Collections.singletonList(possessionHistory))
                .build();

        possessionHistory.setApartment(apartment);

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

        // Verify that mailService and smsService were called
        verify(mailService, times(1)).sendNotificationMail(
                eq("Nowe ogłoszenie"),
                eq("Nowe ogłoszenie zostało oddane: " + announcement.getTitle()),
                eq(resident.getEmail())
        );
        verify(smsService, times(1)).sendSms(
                eq("Nowe ogłoszenie zostało oddane: " + announcement.getTitle()),
                eq(resident.getPhoneNumber())
        );
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

    @Test
    void testGetAnnouncementsByApartmentSignature_Success() {
        // Given
        String apartmentSignature = "A101";
        Apartment apartment = Apartment.builder()
                .uuidID(UUID.randomUUID())
                .apartmentSignature(apartmentSignature)
                .build();

        AnnouncementApartment announcementApartment = AnnouncementApartment.builder()
                .announcement(announcement)
                .apartment(apartment)
                .build();

        List<AnnouncementApartment> announcementApartmentList = Collections.singletonList(announcementApartment);
        Page<AnnouncementApartment> announcementApartmentPage = new PageImpl<>(announcementApartmentList, pageable, 1);

        when(announcementApartmentRepository.findByApartmentSignature(apartmentSignature, pageable))
                .thenReturn(announcementApartmentPage);

        // When
        PageResponse<AnnouncementResponse> response = announcementService.getAnnouncementsByApartmentSignature(apartmentSignature, pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalPages());
        assertEquals(1, response.content().size());
        assertEquals("Test Announcement", response.content().get(0).title());

        verify(announcementApartmentRepository, times(1)).findByApartmentSignature(apartmentSignature, pageable);
    }

    @Test
    void testGetAnnouncementsByApartmentSignature_NotFound() {
        // Given
        String apartmentSignature = "A101";

        Page<AnnouncementApartment> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(announcementApartmentRepository.findByApartmentSignature(apartmentSignature, pageable))
                .thenReturn(emptyPage);

        // When
        PageResponse<AnnouncementResponse> response = announcementService.getAnnouncementsByApartmentSignature(apartmentSignature, pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(0, response.totalPages());
        assertTrue(response.content().isEmpty());

        verify(announcementApartmentRepository, times(1)).findByApartmentSignature(apartmentSignature, pageable);
    }
}
