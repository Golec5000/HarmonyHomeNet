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
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.DocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private Document document;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentId = UUID.randomUUID();
        userId = UUID.randomUUID();

        document = Document.builder()
                .uuidID(documentId)
                .documentName("Test Document")
                .documentType(DocumentType.OTHER)
                .documentData("Test data".getBytes())
                .createdAt(Instant.now())
                .userDocumentConnections(new ArrayList<>())
                .build();

        user = User.builder()
                .uuidID(userId)
                .firstName("TestUser")
                .role(Role.ROLE_OWNER)
                .userDocumentConnections(new ArrayList<>())
                .build();
    }

//    @Test
//    void testGetAllDocuments() {
//        when(documentRepository.findAll()).thenReturn(Collections.singletonList(document));
//
//        List<DocumentResponse> responses = documentService.getAllDocuments();
//
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals(document.getDocumentName(), responses.get(0).documentName());
//
//        verify(documentRepository, times(1)).findAll();
//    }

//    @Test
//    void testGetAllDocumentsByUserId_UserNotFound() {
//        when(userRepository.existsByUuidID(userId)).thenReturn(false);
//
//        assertThrows(UserNotFoundException.class, () -> documentService.getAllDocumentsByUserId(userId));
//
//        verify(userRepository, times(1)).existsByUuidID(userId);
//        verifyNoMoreInteractions(documentRepository);
//    }

    @Test
    void testGetDocumentById_Success() throws DocumentNotFoundException {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        DocumentResponse response = documentService.getDocumentById(documentId);

        assertNotNull(response);
        assertEquals(document.getDocumentName(), response.documentName());

        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testGetDocumentById_DocumentNotFound() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.getDocumentById(documentId));

        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDeleteDocument_DeleteCompletely_Success() throws DocumentNotFoundException, UserNotFoundException {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        List<UserDocumentConnection> connections = new ArrayList<>();
        UserDocumentConnection connection = UserDocumentConnection.builder()
                .document(document)
                .user(user)
                .build();
        connections.add(connection);

        when(userDocumentConnectionRepository.findByDocumentUuidID(documentId)).thenReturn(connections);

        String result = documentService.deleteDocument(documentId, userId, true);

        assertNotNull(result);
        assertEquals("Document id: " + documentId + " deleted successfully for all users", result);

        verify(documentRepository, times(1)).findById(documentId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidID(documentId);
        verify(userDocumentConnectionRepository, times(1)).deleteAll(connections);
        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    void testDeleteDocument_DeleteConnection_Success() throws DocumentNotFoundException, UserNotFoundException {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDocumentConnection connection = UserDocumentConnection.builder()
                .document(document)
                .user(user)
                .build();

        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(connection));

        String result = documentService.deleteDocument(documentId, userId, false);

        assertNotNull(result);
        assertEquals("Document id: " + documentId + " disconnected successfully for user id: " + userId, result);

        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidIDAndUserUuidID(documentId, userId);
        verify(userDocumentConnectionRepository, times(1)).delete(connection);
    }

    @Test
    void testDeleteDocument_DocumentNotFound() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.deleteDocument(documentId, userId, true));

        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDeleteDocument_UserNotFound() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> documentService.deleteDocument(documentId, userId, false));

        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteDocument_ConnectionNotFound() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> documentService.deleteDocument(documentId, userId, false));

        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidIDAndUserUuidID(documentId, userId);
    }

    @Test
    void testDownloadDocument_Success() throws DocumentNotFoundException {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        DocumentResponse response = documentService.downloadDocument(documentId);

        assertNotNull(response);
        assertEquals(document.getDocumentName(), response.documentName());
        assertNotNull(response.documentDataBase64());

        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void testDownloadDocument_DocumentNotFound() {
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.downloadDocument(documentId));

        verify(documentRepository, times(1)).findById(documentId);
    }
}
