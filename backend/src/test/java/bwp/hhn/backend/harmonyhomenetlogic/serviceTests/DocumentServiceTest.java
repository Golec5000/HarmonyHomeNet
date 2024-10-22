package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.DocumentServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DocumentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDocumentConnectionRepository userDocumentConnectionRepository;

    @Mock
    private PossessionHistoryRepository possessionHistoryRepository;

    @InjectMocks
    private DocumentServiceImp documentService;

    private UUID userId;
    private UUID documentId;
    private UUID wrongDocumentId;
    private UUID wrongUserId;
    private User user;
    private Document document;
    private DocumentRequest documentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.fromString("78939c83-42c1-43aa-9204-fb73acbaa599");
        documentId = UUID.fromString("94408c41-8cc6-4fa1-8c8f-b888ca5dde19");
        wrongDocumentId = UUID.fromString("e35ea644-ed5f-47f2-a37c-84ddac4d015c");
        wrongUserId = UUID.fromString("ccc62632-87c5-45fe-8a61-83144a2173b0");

        user = new User();
        user.setUuidID(userId);
        user.setRole(Role.ADMIN);
        user.setAccessLevel(AccessLevel.READ.getLevel() | AccessLevel.WRITE.getLevel() | AccessLevel.DELETE.getLevel());

        document = Document.builder()
                .documentName("Test Document")
                .documentType(DocumentType.OTHER)
                .documentData(new byte[]{1, 2, 3})
                .build();

        documentRequest = DocumentRequest.builder()
                .documentName("Updated Document")
                .documentType(DocumentType.RESOLUTION)
                .documentData(new byte[]{4, 5, 6})
                .build();
    }

    @Test
    void shouldUploadPublicDocumentSuccessfully() throws UserNotFoundException {
        // Mock użytkownika
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock zapisu dokumentu
        DocumentResponse response = documentService.uploadDocument(documentRequest, userId, null);

        // Assercje
        assertThat(response).isNotNull();
        assertThat(response.documentName()).isEqualTo("Updated Document");

        // Weryfikacja zapisów
        verify(documentRepository, times(1)).save(any(Document.class));
        verify(userDocumentConnectionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldUploadPrivateDocumentSuccessfully() throws UserNotFoundException {

        UUID apartmentId = UUID.fromString("9ea15a64-84ba-40b7-bc49-7a25c05deb6f");

        // Mock użytkownika
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock mieszkańców apartamentu
        List<User> residents = List.of(user);
        when(possessionHistoryRepository.findActiveResidentsByApartment(any(UUID.class)))
                .thenReturn(residents);

        // Mock pracowników (opcjonalne)
        List<User> employees = List.of(new User());
        when(userRepository.findAllByRole(Role.EMPLOYEE)).thenReturn(employees);

        // Wywołanie metody
        DocumentRequest privateDocumentRequest = DocumentRequest.builder()
                .documentName("Ownership Document")
                .documentType(DocumentType.PROPERTY_DEED)
                .documentData(new byte[]{4, 5, 6})
                .build();

        DocumentResponse response = documentService.uploadDocument(privateDocumentRequest, userId, apartmentId);

        // Assercje
        assertThat(response).isNotNull();
        assertThat(response.documentName()).isEqualTo("Ownership Document");

        // Weryfikacja, czy wszystko zostało poprawnie zapisane
        verify(documentRepository, times(1)).save(any(Document.class));
        verify(userDocumentConnectionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenNoResidentsInApartment() {
        // Mock użytkownika
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock pustej listy mieszkańców
        when(possessionHistoryRepository.findActiveResidentsByApartment(any(UUID.class)))
                .thenReturn(Collections.emptyList());

        // Próba uploadu prywatnego dokumentu powinna rzucić wyjątek
        DocumentRequest privateDocumentRequest = DocumentRequest.builder()
                .documentName("Ownership Document")
                .documentType(DocumentType.PROPERTY_DEED)
                .documentData(new byte[]{4, 5, 6})
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                documentService.uploadDocument(privateDocumentRequest, userId, UUID.randomUUID())
        );
    }

    @Test
    void shouldGetDocumentByIdSuccessfully() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        DocumentResponse response = documentService.getDocumentById(documentId);

        assertNotNull(response);
        assertEquals("Test Document", response.documentName());

        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void shouldThrowDocumentNotFoundExceptionWhenGettingDocumentById() {
        when(documentRepository.findById(wrongDocumentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.getDocumentById(wrongDocumentId)
        );

    }

    @Test
    void shouldDeleteDocumentCompletely() throws UserNotFoundException, DocumentNotFoundException {
        // Inicjalizacja użytkownika i przypisanie pustej listy połączeń
        user.setUserDocumentConnections(new ArrayList<>());
        document.setUserDocumentConnections(new ArrayList<>());

        // Tworzenie połączenia z przypisanym użytkownikiem i dokumentem
        UserDocumentConnection connection = new UserDocumentConnection();
        connection.setUser(user);  // Przypisanie użytkownika
        connection.setDocument(document);  // Przypisanie dokumentu

        // Dodaj połączenie do listy połączeń użytkownika i dokumentu
        user.getUserDocumentConnections().add(connection);
        document.getUserDocumentConnections().add(connection);

        // Mockowanie wywołań repozytoriów
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userDocumentConnectionRepository.findByDocumentUuidID(documentId))
                .thenReturn(List.of(connection));  // Zwrócenie połączenia

        // Wywołanie metody
        String result = documentService.deleteDocument(documentId, userId, true);

        // Assercje
        assertEquals("Document id: " + documentId + " deleted successfully for all users", result);

        // Weryfikacja usunięcia połączeń i dokumentu
        verify(userDocumentConnectionRepository, times(1)).deleteAll(anyList());
        verify(documentRepository, times(1)).delete(document);
    }


    @Test
    void shouldDisconnectUserFromDocument() throws UserNotFoundException, DocumentNotFoundException {
        // Mock użytkownika i przypisanie pustej listy połączeń
        user.setUserDocumentConnections(new ArrayList<>());
        document.setUserDocumentConnections(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // Mock połączenia użytkownika z dokumentem
        UserDocumentConnection connection = new UserDocumentConnection();
        connection.setUser(user);
        connection.setDocument(document);

        // Dodajemy połączenie do obu obiektów
        user.getUserDocumentConnections().add(connection);
        document.getUserDocumentConnections().add(connection);

        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(connection));

        // Wywołanie metody
        String result = documentService.deleteDocument(documentId, userId, false);

        // Assercje
        assertEquals("Document id: " + documentId + " disconnected successfully for user id: " + userId, result);

        // Weryfikacja usunięcia połączenia
        verify(userDocumentConnectionRepository, times(1)).delete(connection);
    }


    @Test
    void shouldThrowDocumentNotFoundExceptionWhenDeletingNonExistingDocument() {
        when(documentRepository.findById(wrongDocumentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.deleteDocument(wrongDocumentId, userId, true)
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeletingDocument() {
        // Mockujemy brak użytkownika
        when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());

        // Mockujemy, że dokument istnieje (aby upewnić się, że przejdzie przez ten krok)
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // Sprawdzamy, czy zostanie rzucony właściwy wyjątek
        assertThrows(UserNotFoundException.class, () ->
                documentService.deleteDocument(documentId, wrongUserId, true)
        );

        // Weryfikujemy, że dokument nie został usunięty
        verify(documentRepository, never()).delete(any());
        verify(userDocumentConnectionRepository, never()).deleteAll(anyList());
    }

    @Test
    @Transactional
    void shouldReturnAllDocumentsByUserId() throws UserNotFoundException {

        Document doc1 = Document.builder()
                .documentName("Doc 1")
                .documentType(DocumentType.DECISION)
                .documentData("Data 1".getBytes())
                .build();

        Document doc2 = Document.builder()
                .documentName("Doc 2")
                .documentType(DocumentType.PROPERTY_DEED)
                .documentData("Data 2".getBytes())
                .build();

        List<Document> mockDocuments = List.of(doc1, doc2);

        when(userRepository.existsByUuidID(userId)).thenReturn(true);
        when(userDocumentConnectionRepository.findDocumentsByUserId(userId)).thenReturn(mockDocuments);

        List<DocumentResponse> response = documentService.getAllDocumentsByUserId(userId);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(DocumentType.DECISION, response.get(0).documentType());
        assertEquals(DocumentType.PROPERTY_DEED, response.get(1).documentType());
        verify(userRepository).existsByUuidID(userId);
        verify(userDocumentConnectionRepository).findDocumentsByUserId(userId);
    }

    @Test
    @Transactional
    void shouldThrowUserNotFoundExceptionWhenGettingDocumentsByUserId() {

        when(userRepository.existsByUuidID(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                documentService.getAllDocumentsByUserId(userId)
        );

        verify(userRepository).existsByUuidID(userId);
        verifyNoInteractions(userDocumentConnectionRepository);
    }

    @Test
    void shouldDownloadDocumentSuccessfully() throws DocumentNotFoundException {

        document.setUuidID(documentId);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        DocumentResponse response = documentService.downloadDocument(documentId);

        assertNotNull(response);
        assertEquals("Test Document", response.documentName());
        assertEquals(DocumentType.OTHER, response.documentType());
        assertEquals(Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}), response.documentDataBase64());

        verify(documentRepository).findById(documentId);
    }

    @Test
    void shouldThrowDocumentNotFoundExceptionWhenDocumentDoesNotExist() {
        when(documentRepository.findById(wrongDocumentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.downloadDocument(wrongDocumentId)
        );

        verify(documentRepository).findById(wrongDocumentId);
    }


}
