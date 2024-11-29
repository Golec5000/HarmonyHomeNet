package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

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
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.DocumentService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.DocumentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements DocumentService {

    private final UserDocumentConnectionRepository userDocumentConnectionRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final MailService mailService;
    private final SmsService smsService;

    @Override
    public PageResponse<DocumentResponse> getAllDocuments(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Document> documents = documentRepository.findAll(pageable);

        return getDocumentResponsePageResponse(documents);
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, String apartmentSignature, DocumentType documentType) throws IllegalArgumentException, IOException {

        // Tworzenie nowego obiektu dokumentu
        Document documentEntity = Document.builder()
                .documentName(getOriginalFileNameWithoutExtension(file.getOriginalFilename()))
                .documentData(file.getBytes())
                .documentType(documentType)
                .documentExtension(getFileExtension(file.getOriginalFilename()))
                .build();

        // Zapis dokumentu w repozytorium
        documentRepository.save(documentEntity);

        // Pobranie użytkowników do przypisania w zależności od typu dokumentu
        List<User> eligibleUsers;
        if (!documentType.equals(DocumentType.PROPERTY_DEED)) {
            // Dokument publiczny - przypisujemy wszystkich użytkowników
            eligibleUsers = userRepository.findAll();
        } else {
            // Dokument prywatny - pobierz mieszkańców apartamentu i pracowników oraz adminów
            List<User> residents = possessionHistoryRepository.findActiveResidentsByApartment(apartmentSignature);

            if (residents.isEmpty()) {
                throw new IllegalArgumentException("No residents found in apartment with signature: " + apartmentSignature);
            }

            List<User> employees = userRepository.findAllByRole(Role.ROLE_EMPLOYEE);
            List<User> admins = userRepository.findAllByRole(Role.ROLE_ADMIN);

            eligibleUsers = new ArrayList<>(residents);
            eligibleUsers.addAll(employees);
            eligibleUsers.addAll(admins);
        }

        // Tworzenie połączeń dokumentu z wybranymi użytkownikami
        List<UserDocumentConnection> connections = new ArrayList<>();
        for (User user : eligibleUsers) {
            // Sprawdzenie, czy połączenie już istnieje
            if (userDocumentConnectionRepository.existsByDocumentUuidIDAndUserUuidID(documentEntity.getUuidID(), user.getUuidID())) {
                continue; // Pomijamy tworzenie połączenia, jeśli już istnieje
            }

            UserDocumentConnection connection = UserDocumentConnection.builder()
                    .document(documentEntity)
                    .user(user)
                    .build();
            connections.add(connection);

            // Przypisanie połączenia użytkownikowi
            if (user.getUserDocumentConnections() == null) user.setUserDocumentConnections(new ArrayList<>());
            user.getUserDocumentConnections().add(connection);

            // Przypisanie połączenia dokumentowi
            if (documentEntity.getUserDocumentConnections() == null)
                documentEntity.setUserDocumentConnections(new ArrayList<>());

            documentEntity.getUserDocumentConnections().add(connection);
        }

        // Zapis wszystkich połączeń w bazie danych
        userDocumentConnectionRepository.saveAll(connections);

        // Wysyłanie powiadomień do użytkowników na podstawie ich preferencji
        eligibleUsers.forEach(user -> {
            if (user.getNotificationTypes() != null) {
                user.getNotificationTypes().forEach(notificationType -> {
                    switch (notificationType.getType()) {
                        case EMAIL:
                            mailService.sendNotificationMail(
                                    "Nowy dokument",
                                    "Nowy dokument został oddany: " + documentEntity.getDocumentName(),
                                    user.getEmail()
                            );
                            break;
                        case SMS:
                            smsService.sendSms(
                                    "Nowy dokument został oddany: " + documentEntity.getDocumentName(),
                                    user.getPhoneNumber()
                            );
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + notificationType.getType());
                    }
                });
            }
        });

        // Zwracanie odpowiedzi z informacjami o nowo załadowanym dokumencie
        return DocumentResponse.builder()
                .documentName(documentEntity.getDocumentName())
                .documentType(documentEntity.getDocumentType())
                .createdAt(documentEntity.getCreatedAt())
                .documentExtension(documentEntity.getDocumentExtension())
                .build();
    }

    @Override
    public PageResponse<DocumentResponse> getAllDocumentsByUserId(UUID userId, int pageNo, int pageSize) throws UserNotFoundException {
        if (!userRepository.existsByUuidID(userId))
            throw new UserNotFoundException("User id: " + userId + " not found");

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Document> documents = documentRepository.findDocumentsByUserId(userId, pageable);


        return getDocumentResponsePageResponse(documents);

    }

    @Override
    public DocumentResponse getDocumentById(UUID id) throws DocumentNotFoundException {
        return documentRepository.findById(id)
                .map(
                        document -> DocumentResponse.builder()
                                .documentName(document.getDocumentName())
                                .documentType(document.getDocumentType())
                                .createdAt(document.getCreatedAt())
                                .build()
                )
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + id + " not found"));
    }

    @Override
    @Transactional
    public String deleteDocument(UUID documentId, UUID userId, Boolean deleteCompletely) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + documentId + " not found"));

        if (deleteCompletely) {

            documentRepository.delete(document);
            return "Document id: " + documentId + " deleted successfully for all users";

        } else {

            // Usuwanie tylko połączenia użytkownika z dokumentem
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

            UserDocumentConnection connection = userDocumentConnectionRepository
                    .findByDocumentUuidIDAndUserUuidID(documentId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

            // Inicjalizujemy listy, jeśli są nullem, aby uniknąć NullPointerException
            if (user.getUserDocumentConnections() == null) user.setUserDocumentConnections(new ArrayList<>());
            if (document.getUserDocumentConnections() == null) document.setUserDocumentConnections(new ArrayList<>());

            // Usunięcie połączenia
            user.getUserDocumentConnections().remove(connection);
            document.getUserDocumentConnections().remove(connection);

            userDocumentConnectionRepository.delete(connection);

            mailService.sendNotificationMail(
                    "Dokument odłączony",
                    "Dokument został odłaczony od twojego konta: " + document.getDocumentName(),
                    user.getEmail()
            );

            smsService.sendSms(
                    "Dokument został odłaczony od twojego konta: " + document.getDocumentName(),
                    user.getPhoneNumber()
            );

            return "Document id: " + documentId + " disconnected successfully for user id: " + userId;
        }
    }

    @Override
    public DocumentResponse downloadDocument(UUID documentId) throws DocumentNotFoundException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + documentId + " not found"));

        return DocumentResponse.builder()
                .documentName(document.getDocumentName())
                .documentType(document.getDocumentType())
                .documentDataBase64(document.getDocumentData())
                .createdAt(document.getCreatedAt())
                .build();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            throw new IllegalArgumentException("Invalid file name");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String getOriginalFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            throw new IllegalArgumentException("Invalid file name");
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private PageResponse<DocumentResponse> getDocumentResponsePageResponse(Page<Document> documents) {
        return new PageResponse<>(
                documents.getNumber(),
                documents.getSize(),
                documents.getTotalPages(),
                documents.getContent().stream()
                        .map(
                                document -> DocumentResponse.builder()
                                        .documentId(document.getUuidID())
                                        .documentName(document.getDocumentName())
                                        .documentType(document.getDocumentType())
                                        .documentExtension(document.getDocumentExtension())
                                        .createdAt(document.getCreatedAt())
                                        .build()
                        )
                        .toList(),
                documents.isLast(),
                documents.hasNext(),
                documents.hasPrevious()
        );
    }

}