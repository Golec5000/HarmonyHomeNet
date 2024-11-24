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
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.DocumentServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.DocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
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

    @Mock
    private MailService mailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private DocumentServiceImp documentService;

    private UUID documentId;
    private UUID userId;
    private Document document;
    private User user;
    private UserDocumentConnection connection;

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
                .documentExtension("txt")
                .createdAt(Instant.now())
                .userDocumentConnections(new ArrayList<>())
                .build();

        user = User.builder()
                .uuidID(userId)
                .firstName("TestUser")
                .lastName("UserLastName")
                .email("testuser@example.com")
                .role(Role.ROLE_OWNER)
                .userDocumentConnections(new ArrayList<>())
                .build();

        connection = UserDocumentConnection.builder()
                .document(document)
                .user(user)
                .build();

        document.getUserDocumentConnections().add(connection);
        user.getUserDocumentConnections().add(connection);
    }

    @Test
    void testGetAllDocuments_Success() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Document> documents = Collections.singletonList(document);
        Page<Document> documentPage = new PageImpl<>(documents, pageable, documents.size());

        when(documentRepository.findAll(pageable)).thenReturn(documentPage);

        // When
        PageResponse<DocumentResponse> responses = documentService.getAllDocuments(pageNo, pageSize);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.content().size());
        assertEquals(document.getDocumentName(), responses.content().get(0).documentName());

        verify(documentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllDocumentsByUserId_Success() throws UserNotFoundException {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        when(userRepository.existsByUuidID(userId)).thenReturn(true);
        List<Document> documents = Collections.singletonList(document);
        Page<Document> documentPage = new PageImpl<>(documents, pageable, documents.size());

        when(documentRepository.findDocumentsByUserId(userId, pageable)).thenReturn(documentPage);

        // When
        PageResponse<DocumentResponse> responses = documentService.getAllDocumentsByUserId(userId, pageNo, pageSize);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.content().size());
        assertEquals(document.getDocumentName(), responses.content().get(0).documentName());

        verify(userRepository, times(1)).existsByUuidID(userId);
        verify(documentRepository, times(1)).findDocumentsByUserId(userId, pageable);
    }

    @Test
    void testGetAllDocumentsByUserId_UserNotFound() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        when(userRepository.existsByUuidID(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> documentService.getAllDocumentsByUserId(userId, pageNo, pageSize));

        verify(userRepository, times(1)).existsByUuidID(userId);
        verifyNoInteractions(documentRepository);
    }

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

        String result = documentService.deleteDocument(documentId, userId, true);

        assertNotNull(result);
        assertEquals("Document id: " + documentId + " deleted successfully for all users", result);

        verify(documentRepository, times(1)).findById(documentId);
        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    void testDeleteDocument_DeleteConnection_Success() throws DocumentNotFoundException, UserNotFoundException {
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId))
                .thenReturn(Optional.of(connection));

        String result = documentService.deleteDocument(documentId, userId, false);

        assertNotNull(result);
        assertEquals("Document id: " + documentId + " disconnected successfully for user id: " + userId, result);

        verify(documentRepository, times(1)).findById(documentId);
        verify(userRepository, times(1)).findById(userId);
        verify(userDocumentConnectionRepository, times(1)).findByDocumentUuidIDAndUserUuidID(documentId, userId);
        verify(userDocumentConnectionRepository, times(1)).delete(connection);
        verify(mailService, times(1)).sendNotificationMail(
                anyString(), anyString(), eq(user.getEmail()));
        verify(smsService, times(1)).sendSms(
                anyString(), eq(user.getPhoneNumber()));
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

    @Test
    void testUploadDocument_Success() throws IOException {
        // Given
        String apartmentSignature = "A101";
        DocumentType documentType = DocumentType.OTHER;
        byte[] fileData = "Test File Data".getBytes();
        String fileName = "testfile.txt";
        String fileExtension = "txt";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileData);

        Document savedDocument = Document.builder()
                .uuidID(UUID.randomUUID())
                .documentName("testfile")
                .documentExtension(fileExtension)
                .documentType(documentType)
                .documentData(fileData)
                .createdAt(Instant.now())
                .build();

        when(documentRepository.save(any(Document.class))).thenReturn(savedDocument);

        List<User> eligibleUsers = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(eligibleUsers);

        // When
        DocumentResponse response = documentService.uploadDocument(file, apartmentSignature, documentType);

        // Then
        assertNotNull(response);
        assertEquals("testfile", response.documentName());
        verify(documentRepository, times(1)).save(any(Document.class));
        verify(userDocumentConnectionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadDocument_InvalidFileName() {
        // Given
        String apartmentSignature = "A101";
        DocumentType documentType = DocumentType.OTHER;

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("invalidfilename"); // No extension

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(file, apartmentSignature, documentType));
    }

    @Test
    void testUploadDocument_PropertyDeed_NoResidents() throws IOException {
        // Given
        String apartmentSignature = "A101";
        DocumentType documentType = DocumentType.PROPERTY_DEED;
        byte[] fileData = "Test File Data".getBytes();
        String fileName = "testfile.txt";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileData);

        when(possessionHistoryRepository.findActiveResidentsByApartment(apartmentSignature)).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(file, apartmentSignature, documentType));
    }
}
