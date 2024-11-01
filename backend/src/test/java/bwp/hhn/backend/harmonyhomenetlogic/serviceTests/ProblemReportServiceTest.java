package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.ProblemReportServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ProblemReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProblemReportServiceTest {

    @Mock
    private ProblemReportRepository problemReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApartmentsRepository apartmentsRepository;

    @Mock
    private PossessionHistoryRepository possessionHistoryRepository;

    @InjectMocks
    private ProblemReportServiceImp problemReportService;

    private User user;
    private Apartment apartment;
    private ProblemReport problemReport;
    private ProblemReportRequest problemReportRequest;
    private UUID userId;
    private UUID apartmentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        apartmentId = UUID.randomUUID();

        user = User.builder()
                .uuidID(userId)
                .firstName("John")
                .lastName("Doe")
                .build();

        apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentSignature("A101")
                .address("123 Main St")
                .build();

        problemReport = ProblemReport.builder()
                .id(1L)
                .note("Leaky faucet")
                .reportStatus(ReportStatus.OPEN)
                .category(Category.GENERAL)
                .user(user)
                .apartment(apartment)
                .build();

        problemReportRequest = ProblemReportRequest.builder()
                .userId(userId)
                .apartmentSignature("A101")
                .note("Leaky faucet")
                .reportStatus(ReportStatus.OPEN)
                .category(Category.GENERAL)
                .build();
    }

    @Test
    void testCreateProblemReport_Success() throws UserNotFoundException, ApartmentNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartmentId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(problemReportRepository.save(any(ProblemReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProblemReportResponse response = problemReportService.createProblemReport(problemReportRequest);

        // Then
        assertNotNull(response);
        assertEquals("Leaky faucet", response.note());
        assertEquals("John Doe", response.userName());
        assertEquals("123 Main St", response.apartmentAddress());

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(userId, apartmentId);
        verify(userRepository, times(1)).findById(userId);
        verify(problemReportRepository, times(1)).save(any(ProblemReport.class));
    }

    @Test
    void testCreateProblemReport_ApartmentNotFound() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> problemReportService.createProblemReport(problemReportRequest));

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verifyNoMoreInteractions(possessionHistoryRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testCreateProblemReport_UserNotAuthorized() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartmentId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> problemReportService.createProblemReport(problemReportRequest));

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(userId, apartmentId);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testUpdateProblemReport_Success() throws ProblemReportNotFoundException {
        // Given
        ProblemReportRequest updateRequest = ProblemReportRequest.builder()
                .note("Fixed leaky faucet")
                .reportStatus(ReportStatus.DONE)
                .category(Category.GENERAL)
                .build();

        when(problemReportRepository.findById(1L)).thenReturn(Optional.of(problemReport));
        when(problemReportRepository.save(any(ProblemReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProblemReportResponse response = problemReportService.updateProblemReport(1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("Fixed leaky faucet", response.note());
        assertEquals(ReportStatus.DONE, response.reportStatus());
        assertNotNull(response.endDate());

        verify(problemReportRepository, times(1)).findById(1L);
        verify(problemReportRepository, times(1)).save(any(ProblemReport.class));
    }

    @Test
    void testUpdateProblemReport_NotFound() {
        // Given
        ProblemReportRequest updateRequest = ProblemReportRequest.builder()
                .note("Fixed leaky faucet")
                .reportStatus(ReportStatus.DONE)
                .category(Category.GENERAL)
                .build();

        when(problemReportRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProblemReportNotFoundException.class, () -> problemReportService.updateProblemReport(1L, updateRequest));

        verify(problemReportRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testDeleteProblemReport_Success() throws ProblemReportNotFoundException {
        // Given
        when(problemReportRepository.existsById(1L)).thenReturn(true);
        doNothing().when(problemReportRepository).deleteById(1L);

        // When
        String result = problemReportService.deleteProblemReport(1L);

        // Then
        assertEquals("Problem report deleted successfully", result);
        verify(problemReportRepository, times(1)).existsById(1L);
        verify(problemReportRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProblemReport_NotFound() {
        // Given
        when(problemReportRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ProblemReportNotFoundException.class, () -> problemReportService.deleteProblemReport(1L));

        verify(problemReportRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testGetProblemReportById_Success() throws ProblemReportNotFoundException {
        // Given
        when(problemReportRepository.findById(1L)).thenReturn(Optional.of(problemReport));

        // When
        ProblemReportResponse response = problemReportService.getProblemReportById(1L);

        // Then
        assertNotNull(response);
        assertEquals("Leaky faucet", response.note());
        assertEquals("John Doe", response.userName());
        verify(problemReportRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProblemReportById_NotFound() {
        // Given
        when(problemReportRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProblemReportNotFoundException.class, () -> problemReportService.getProblemReportById(1L));

        verify(problemReportRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProblemReportsByUserId_Success() throws UserNotFoundException {
        // Given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(problemReportRepository.findAllByUserUuidID(userId)).thenReturn(Collections.singletonList(problemReport));

        // When
        List<ProblemReportResponse> responses = problemReportService.getProblemReportsByUserId(userId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Leaky faucet", responses.get(0).note());
        verify(userRepository, times(1)).existsById(userId);
        verify(problemReportRepository, times(1)).findAllByUserUuidID(userId);
    }

    @Test
    void testGetProblemReportsByUserId_UserNotFound() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> problemReportService.getProblemReportsByUserId(userId));

        verify(userRepository, times(1)).existsById(userId);
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testGetProblemReportsByApartmentSignature_Success() throws ApartmentNotFoundException {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.of(apartment));
        when(problemReportRepository.findAllByApartmentUuidID(apartmentId)).thenReturn(Collections.singletonList(problemReport));

        // When
        List<ProblemReportResponse> responses = problemReportService.getProblemReportsByApartmentSignature("A101");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Leaky faucet", responses.get(0).note());
        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verify(problemReportRepository, times(1)).findAllByApartmentUuidID(apartmentId);
    }

    @Test
    void testGetProblemReportsByApartmentSignature_NotFound() {
        // Given
        when(apartmentsRepository.findByApartmentSignature("A101")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApartmentNotFoundException.class, () -> problemReportService.getProblemReportsByApartmentSignature("A101"));

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A101");
        verifyNoMoreInteractions(problemReportRepository);
    }

    @Test
    void testGetAllProblemReports() {
        // Given
        when(problemReportRepository.findAll()).thenReturn(Collections.singletonList(problemReport));

        // When
        List<ProblemReportResponse> responses = problemReportService.getAllProblemReports();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Leaky faucet", responses.get(0).note());
        verify(problemReportRepository, times(1)).findAll();
    }

    @Test
    void testGetProblemReportsByStatus_Success() {
        // Given
        when(problemReportRepository.findAllByReportStatus(ReportStatus.OPEN)).thenReturn(Collections.singletonList(problemReport));

        // When
        List<ProblemReportResponse> responses = problemReportService.getProblemReportsByStatus(ReportStatus.OPEN);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Leaky faucet", responses.get(0).note());
        verify(problemReportRepository, times(1)).findAllByReportStatus(ReportStatus.OPEN);
    }

    @Test
    void testGetProblemReportsByStatus_Empty() {
        // Given
        when(problemReportRepository.findAllByReportStatus(ReportStatus.DONE)).thenReturn(Collections.emptyList());

        // When
        List<ProblemReportResponse> responses = problemReportService.getProblemReportsByStatus(ReportStatus.DONE);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(problemReportRepository, times(1)).findAllByReportStatus(ReportStatus.DONE);
    }
}
