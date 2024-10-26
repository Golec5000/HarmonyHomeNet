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
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.DocumentServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private UserDocumentConnectionRepository userDocumentConnectionRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PossessionHistoryRepository possessionHistoryRepository;

    @InjectMocks
    private DocumentServiceImp documentService;

    private UUID documentId;
    private UUID userId;
    private UUID apartmentId;
    private Document document;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        apartmentId = UUID.randomUUID();

        document = Document.builder()
                .uuidID(documentId)
                .documentName("Test Document")
                .documentType(DocumentType.OTHER)
                .documentData("Test data".getBytes())
                .createdAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .uuidID(userId)
                .firstName("TestUser")
                .role(Role.USER)
                .userDocumentConnections(new ArrayList<>())
                .build();
    }

    @Test
    void testGetAllDocumentsByUserId_Success() throws UserNotFoundException {
        // Given
        when(userRepository.existsByUuidID(userId)).thenReturn(true);
        when(userDocumentConnectionRepository.findDocumentsByUserId(userId)).thenReturn(Collections.singletonList(document));

        // When
        List<DocumentResponse> responses = documentService.getAllDocumentsByUserId(userId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(document.getDocumentName(), responses.get(0).documentName());
        verify(userRepository, times(1)).existsByUuidID(userId);
        verify(userDocumentConnectionRepository, times(1)).findDocumentsByUserId(userId);
    }

    @Test
    void testGetAllDocumentsByUserId_UserNotFound() {
        // Given
        when(userRepository.existsByUuidID(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            documentService.getAllDocumentsByUserId(userId);
        });
        verify(userRepository, times(1)).existsByUuidID(userId);
        verifyNoMoreInteractions(userDocumentConnectionRepository);
    }

    @Test
    void testGetDocumentById_Success() throws DocumentNotFoundException {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // When
        DocumentResponse response = documentService.getDocumentById(documentId);

        // Then
        assertNotNull(response);
        assertEquals(document.getDocumentName(), response.documentName());
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testGetDocumentById_DocumentNotFound() {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DocumentNotFoundException.class, () -> {
            documentService.getDocumentById(documentId);
        });
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDeleteDocument_DeleteCompletely_Success() throws DocumentNotFoundException, UserNotFoundException {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        List<UserDocumentConnection> connections = new ArrayList<>();
        UserDocumentConnection connection = UserDocumentConnection.builder()
                .document(document)
                .user(user)
                .build();
        connections.add(connection);
        when(userDocumentConnectionRepository.findByDocumentUuidID(documentId)).thenReturn(connections);
        doNothing().when(userDocumentConnectionRepository).deleteAll(connections);
        doNothing().when(documentRepository).delete(document);

        // When
        String result = documentService.deleteDocument(documentId, userId, true);

        // Then
        assertNotNull(result);
        assertEquals("Document id: " + documentId + " deleted successfully for all users", result);
        verify(documentRepository, times(1)).findById(documentId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidID(documentId);
        verify(userDocumentConnectionRepository, times(1)).deleteAll(connections);
        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    void testDeleteDocument_DeleteConnection_Success() throws DocumentNotFoundException, UserNotFoundException {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDocumentConnection connection = UserDocumentConnection.builder()
                .document(document)
                .user(user)
                .build();
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(connection));
        doNothing().when(userDocumentConnectionRepository).delete(connection);

        // When
        String result = documentService.deleteDocument(documentId, userId, false);

        // Then
        assertNotNull(result);
        assertEquals("Document id: " + documentId + " disconnected successfully for user id: " + userId, result);
        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidIDAndUserUuidID(documentId, userId);
        verify(userDocumentConnectionRepository, times(1)).delete(connection);
    }

    @Test
    void testDeleteDocument_DocumentNotFound() {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DocumentNotFoundException.class, () -> {
            documentService.deleteDocument(documentId, userId, true);
        });
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDeleteDocument_UserNotFound() {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            documentService.deleteDocument(documentId, userId, false);
        });
        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteDocument_ConnectionNotFound() {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            documentService.deleteDocument(documentId, userId, false);
        });
        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidIDAndUserUuidID(documentId, userId);
    }

    @Test
    void testDownloadDocument_Success() throws DocumentNotFoundException {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // When
        DocumentResponse response = documentService.downloadDocument(documentId);

        // Then
        assertNotNull(response);
        assertEquals(document.getDocumentName(), response.documentName());
        assertNotNull(response.documentDataBase64());
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDownloadDocument_DocumentNotFound() {
        // Given
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DocumentNotFoundException.class, () -> {
            documentService.downloadDocument(documentId);
        });
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDownloadDocument_NullData() {
        // Given
        document.setDocumentData(null);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            documentService.downloadDocument(documentId);
        });
        verify(documentRepository, times(1)).findById(documentId);
    }
}
