package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface DocumentService {

    PageResponse<DocumentResponse> getAllDocuments(int pageNo, int pageSize);

    DocumentResponse uploadDocument(MultipartFile file, String apartmentSignature, DocumentType documentType) throws IllegalArgumentException, IOException;

    PageResponse<DocumentResponse> getAllDocumentsByUserId(UUID userId, int pageNo, int pageSize) throws UserNotFoundException;

    DocumentResponse getDocumentById(UUID documentId) throws DocumentNotFoundException;

    String deleteDocument(UUID documentId, UUID userId, Boolean deleteCompletely) throws DocumentNotFoundException, UserNotFoundException, IllegalArgumentException;

    DocumentResponse downloadDocument(UUID documentId) throws DocumentNotFoundException;
}
