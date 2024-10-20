package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DocumentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {


    DocumentResponse uploadDocument(DocumentRequest document, UUID userId, UUID apartmentId) throws UserNotFoundException, IllegalArgumentException ;


    List<DocumentResponse> getAllDocumentsByUserId(UUID userId) throws UserNotFoundException;


    DocumentResponse getDocumentById(UUID documentId) throws DocumentNotFoundException;


    DocumentResponse updateDocument(UUID documentId, UUID userId, DocumentRequest document) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException;


    String deleteDocument(UUID documentId, UUID userId, boolean deleteCompletely) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException;


    DocumentResponse downloadDocument(UUID documentId) throws DocumentNotFoundException;
}
