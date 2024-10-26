package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponse uploadDocument(MultipartFile file, UUID apartmentId, DocumentType documentType) throws IllegalArgumentException, IOException;


    List<DocumentResponse> getAllDocumentsByUserId(UUID userId) throws UserNotFoundException;


    DocumentResponse getDocumentById(UUID documentId) throws DocumentNotFoundException;


    String deleteDocument(UUID documentId, UUID userId, boolean deleteCompletely) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException;


    DocumentResponse downloadDocument(UUID documentId) throws DocumentNotFoundException;
}
