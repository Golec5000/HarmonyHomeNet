package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.DocumentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.UserDocumentConnectionRepository;
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

    @Override
    @Transactional
    public DocumentResponse uploadDocument(DocumentRequest document, UUID userId) throws UserNotFoundException, IllegalArgumentException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (!AccessLevel.hasPermission(user.getAccessLevel(), AccessLevel.WRITE))
            throw new IllegalArgumentException("User does not have permission to upload document");

        Document documentEntity = Document.builder()
                .documentName(document.getDocumentName())
                .documentType(document.getDocumentType())
                .documentData(document.getDocumentData())
                .build();

        UserDocumentConnection connection = UserDocumentConnection.builder()
                .document(documentEntity)
                .user(user)
                .build();

        if (documentEntity.getUserDocumentConnections() == null)
            documentEntity.setUserDocumentConnections(new ArrayList<>());
        documentEntity.getUserDocumentConnections().add(connection);

        documentRepository.save(documentEntity);

        if (user.getUserDocumentConnections() == null) user.setUserDocumentConnections(new ArrayList<>());
        user.getUserDocumentConnections().add(connection);

        userRepository.save(user);

        userDocumentConnectionRepository.save(connection);

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
    public String deleteDocument(UUID documentId, UUID userId)
            throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document id: " + documentId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id: " + userId + " not found"));

        if (!AccessLevel.hasPermission(user.getAccessLevel(), AccessLevel.DELETE))
            throw new IllegalArgumentException("User does not have permission to delete document");

        UserDocumentConnection connection = userDocumentConnectionRepository
                .findByDocumentUuidIDAndUserUuidID(documentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        if (document.getUserDocumentConnections() == null) document.setUserDocumentConnections(new ArrayList<>());
        else document.getUserDocumentConnections().remove(connection);

        if (user.getUserDocumentConnections() == null) user.setUserDocumentConnections(new ArrayList<>());
        else user.getUserDocumentConnections().remove(connection);

        documentRepository.deleteById(documentId);
        userDocumentConnectionRepository.delete(connection);

        userRepository.save(user);

        return "Document id: " + documentId + " deleted successfully";
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