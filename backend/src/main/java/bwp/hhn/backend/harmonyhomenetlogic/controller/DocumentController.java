package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.DocumentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.DocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload-document")
    public ResponseEntity<DocumentResponse> uploadDocument(@RequestParam("file") MultipartFile file, @RequestParam UUID apartmentId, @RequestParam DocumentType documentType) throws IllegalArgumentException, IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file, apartmentId, documentType));
    }

    @DeleteMapping("/delete-document/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable UUID documentId, @RequestParam UUID userId, @RequestParam boolean deleteCompletely) throws DocumentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(documentService.deleteDocument(documentId, userId, deleteCompletely));
    }

    @GetMapping("/get-document-by-id/{documentId}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID documentId) throws DocumentNotFoundException {
        return ResponseEntity.ok(documentService.getDocumentById(documentId));
    }

    @GetMapping("/download-document/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable UUID documentId) throws DocumentNotFoundException {
        DocumentResponse documentResponse = documentService.downloadDocument(documentId);

        ByteArrayResource resource = new ByteArrayResource(documentResponse.documentDataBase64());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + documentResponse.documentName() + "\"")
                .body(resource);
    }

}