package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.DocumentServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DocumentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                .documentType("application/pdf")
                .documentData(new byte[]{1, 2, 3})
                .build();

        documentRequest = DocumentRequest.builder()
                .documentName("Updated Document")
                .documentType("application/pdf")
                .documentData(new byte[]{4, 5, 6})
                .build();
    }

    @Test
    void shouldUploadDocumentSuccessfully() throws UserNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        DocumentResponse response = documentService.uploadDocument(documentRequest, userId);

        assertThat(response).isNotNull();
        assertThat(response.documentName()).isEqualTo("Updated Document");

        verify(documentRepository, times(1)).save(any(Document.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userDocumentConnectionRepository, times(1)).save(any(UserDocumentConnection.class));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUploadingDocument() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                documentService.uploadDocument(documentRequest, userId)
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
    void shouldDeleteDocumentSuccessfully() throws UserNotFoundException, DocumentNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(new UserDocumentConnection()));

        String result = documentService.deleteDocument(documentId, userId);

        assertEquals("Document id: " + documentId + " deleted successfully", result);

        verify(documentRepository, times(1)).deleteById(documentId);
        verify(userDocumentConnectionRepository, times(1)).delete(any(UserDocumentConnection.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Transactional
    void shouldThrowDocumentNotFoundExceptionWhenDeletingNonExistingDocument() {

        User mockUser = new User();
        mockUser.setUuidID(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(documentRepository.findById(wrongDocumentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.deleteDocument(wrongDocumentId, userId)
        );
    }

    @Test
    @Transactional
    void shouldThrowUserNotFoundExceptionWhenDeletingDocument() {

        when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());

        Document mockDocument = new Document();
        mockDocument.setUuidID(documentId);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(mockDocument));

        assertThrows(UserNotFoundException.class, () ->
                documentService.deleteDocument(documentId, wrongUserId)
        );
    }

    @Test
    @Transactional
    void shouldUpdateDocumentSuccessfully() throws UserNotFoundException, DocumentNotFoundException {

        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        DocumentRequest documentRequest = new DocumentRequest("Test Document", "application/pdf", "Sample data".getBytes());

        User mockUser = new User();
        mockUser.setUuidID(userId);
        mockUser.setAccessLevel(AccessLevel.WRITE.getLevel());

        Document mockDocument = new Document();
        mockDocument.setUuidID(documentId);
        mockDocument.setDocumentName("Old Name");

        UserDocumentConnection mockConnection = new UserDocumentConnection();
        mockConnection.setUser(mockUser);
        mockConnection.setDocument(mockDocument);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(mockDocument));
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(mockConnection));

        DocumentResponse response = documentService.updateDocument(documentId, userId, documentRequest);

        assertNotNull(response);
        assertEquals("Test Document", response.documentName());
        assertEquals("application/pdf", response.documentType());
        verify(documentRepository).findById(documentId);
        verify(userRepository).findById(userId);
        verify(userDocumentConnectionRepository).save(mockConnection);
    }

    @Test
    @Transactional
    void shouldThrowUserNotFoundExceptionWhenUpdatingDocument() {
        DocumentRequest documentRequest = new DocumentRequest("Test Document", "application/pdf", "Sample data".getBytes());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                documentService.updateDocument(documentId, userId, documentRequest)
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(documentRepository);
    }

    @Test
    @Transactional
    void shouldThrowDocumentNotFoundExceptionWhenUpdatingDocument() {
        DocumentRequest documentRequest = new DocumentRequest("Test Document", "PDF", "Sample data".getBytes());

        User mockUser = new User();
        mockUser.setUuidID(userId);
        mockUser.setAccessLevel(AccessLevel.WRITE.getLevel());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.updateDocument(documentId, userId, documentRequest)
        );

        verify(documentRepository).findById(documentId);
    }

    @Test
    @Transactional
    void shouldReturnAllDocumentsByUserId() throws UserNotFoundException {

        Document doc1 = Document.builder()
                .documentName("Doc 1")
                .documentType("PDF")
                .documentData("Data 1".getBytes())
                .build();

        Document doc2 = Document.builder()
                .documentName("Doc 2")
                .documentType("DOCX")
                .documentData("Data 2".getBytes())
                .build();

        List<Document> mockDocuments = List.of(doc1, doc2);

        when(userRepository.existsByUuidID(userId)).thenReturn(true);
        when(userDocumentConnectionRepository.findDocumentsByUserId(userId)).thenReturn(mockDocuments);

        List<DocumentResponse> response = documentService.getAllDocumentsByUserId(userId);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("PDF", response.get(0).documentType());
        assertEquals("DOCX", response.get(1).documentType());
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
        assertEquals("application/pdf", response.documentType());
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
