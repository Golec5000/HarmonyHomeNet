package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DocumentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.AccessLevel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements DocumentService {

    private final UserDocumentConnectionRepository userDocumentConnectionRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(DocumentRequest document, UUID userId, UUID apartmentId)
            throws UserNotFoundException, IllegalArgumentException {

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (!AccessLevel.hasPermission(uploader.getAccessLevel(), AccessLevel.WRITE)) {
            throw new IllegalArgumentException("User does not have permission to upload document");
        }

        Document documentEntity = Document.builder()
                .documentName(document.getDocumentName())
                .documentType(document.getDocumentType())
                .documentData(document.getDocumentData())
                .build();

        documentRepository.save(documentEntity);  // Zapis dokumentu

        // Pobranie użytkowników do przypisania w zależności od typu dokumentu
        List<User> eligibleUsers;
        if (!document.getDocumentType().equals(DocumentType.PROPERTY_DEED)) {
            // Dokument publiczny - przypisujemy wszystkich użytkowników
            eligibleUsers = userRepository.findAll();
        } else {
            // Dokument prywatny - pobierz mieszkańców apartamentu i pracowników
            List<User> residents = possessionHistoryRepository.findActiveResidentsByApartment(apartmentId);

            if (residents.isEmpty()) {
                throw new IllegalArgumentException("No residents found in apartment id: " + apartmentId);
            }

            List<User> employees = userRepository.findAllByRole(Role.EMPLOYEE);

            eligibleUsers = new ArrayList<>(residents);
            eligibleUsers.addAll(employees);
        }

        // Tworzenie połączeń dokumentu z wybranymi użytkownikami
        List<UserDocumentConnection> connections = new ArrayList<>();
        for (User user : eligibleUsers) {
            UserDocumentConnection connection = UserDocumentConnection.builder()
                    .document(documentEntity)
                    .user(user)
                    .build();
            connections.add(connection);

            // Przypisz połączenie użytkownikowi
            if (user.getUserDocumentConnections() == null) user.setUserDocumentConnections(new ArrayList<>());
            user.getUserDocumentConnections().add(connection);

            // Przypisz połączenie dokumentowi
            if (documentEntity.getUserDocumentConnections() == null) documentEntity.setUserDocumentConnections(new ArrayList<>());
            documentEntity.getUserDocumentConnections().add(connection);
        }

        // Zapis wszystkich połączeń w bazie danych
        userDocumentConnectionRepository.saveAll(connections);

        return DocumentResponse.builder()
                .documentName(documentEntity.getDocumentName())
                .documentType(documentEntity.getDocumentType())
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .build();
    }

    @Override
    public List<DocumentResponse> getAllDocumentsByUserId(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsByUuidID(userId))
            throw new UserNotFoundException("User id: " + userId + " not found");

        return userDocumentConnectionRepository.findDocumentsByUserId(userId).stream()
                .map(
                        document -> DocumentResponse.builder()
                                .documentName(document.getDocumentName())
                                .documentType(document.getDocumentType())
                                .createdAt(document.getCreatedAt())
                                .updatedAt(document.getUpdatedAt())
                                .build()
                )
                .toList();
    }

    @Override
    public DocumentResponse getDocumentById(UUID id) throws DocumentNotFoundException {
        return documentRepository.findById(id)
                .map(
                        document -> DocumentResponse.builder()
                                .documentName(document.getDocumentName())
                                .documentType(document.getDocumentType())
                                .createdAt(document.getCreatedAt())
                                .updatedAt(document.getUpdatedAt())
                                .build()
                )
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + id + " not found"));
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(UUID documentId, UUID userId, DocumentRequest document) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (!AccessLevel.hasPermission(user.getAccessLevel(), AccessLevel.WRITE))
            throw new IllegalArgumentException("User does not have permission to update document");

        Document documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + documentId + " not found"));

        documentEntity.setDocumentName(document.getDocumentName());
        documentEntity.setDocumentType(document.getDocumentType());
        documentEntity.setDocumentData(document.getDocumentData());

        userDocumentConnectionRepository.findByDocumentUuidIDAndUserUuidID(documentId, userId)
                .ifPresent(connection -> {
                    connection.setDocument(documentEntity);
                    connection.setUser(user);
                    userDocumentConnectionRepository.save(connection);
                });

        return DocumentResponse.builder()
                .documentName(documentEntity.getDocumentName())
                .documentType(documentEntity.getDocumentType())
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public String deleteDocument(UUID documentId, UUID userId, boolean deleteCompletely)
            throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + documentId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (!AccessLevel.hasPermission(user.getAccessLevel(), AccessLevel.DELETE)) {
            throw new IllegalArgumentException("User does not have permission to delete document");
        }

        if (deleteCompletely) {
            // Usuwanie dokumentu i wszystkich powiązań
            List<UserDocumentConnection> connections = userDocumentConnectionRepository.findByDocumentUuidID(documentId);
            for (UserDocumentConnection connection : connections) {
                if (connection.getUser() != null && connection.getUser().getUserDocumentConnections() != null) {
                    connection.getUser().getUserDocumentConnections().remove(connection);
                }
                if (connection.getDocument() != null && connection.getDocument().getUserDocumentConnections() != null) {
                    connection.getDocument().getUserDocumentConnections().remove(connection);
                }
            }

            userDocumentConnectionRepository.deleteAll(connections);
            documentRepository.delete(document);

            return "Document id: " + documentId + " deleted successfully for all users";
        } else {
            // Usuwanie tylko połączenia użytkownika z dokumentem
            UserDocumentConnection connection = userDocumentConnectionRepository
                    .findByDocumentUuidIDAndUserUuidID(documentId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

            // Inicjalizujemy listy, jeśli są nullem, aby uniknąć NullPointerException
            if (user.getUserDocumentConnections() == null) {
                user.setUserDocumentConnections(new ArrayList<>());
            }
            if (document.getUserDocumentConnections() == null) {
                document.setUserDocumentConnections(new ArrayList<>());
            }

            // Usunięcie połączenia
            user.getUserDocumentConnections().remove(connection);
            document.getUserDocumentConnections().remove(connection);

            userDocumentConnectionRepository.delete(connection);

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
                .documentDataBase64(encodeToBase64(document.getDocumentData()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private String encodeToBase64(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("Data is null");
        return Base64.getEncoder().encodeToString(data);
    }


}