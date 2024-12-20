package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.DocumentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.DocumentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DocumentDeleteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.DocumentResponse;
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

    //GET
    @GetMapping("/get-all-documents")
    public ResponseEntity<PageResponse<DocumentResponse>> getAllDocuments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(documentService.getAllDocuments(pageNo, pageSize));
    }

    @GetMapping("/get-document-by-id")
    public ResponseEntity<DocumentResponse> getDocumentById(@RequestParam UUID documentId) throws DocumentNotFoundException {
        return ResponseEntity.ok(documentService.getDocumentById(documentId));
    }

    @GetMapping("/get-all-documents-by-user-id")
    public ResponseEntity<PageResponse<DocumentResponse>> getAllDocumentsByUserId(
            @RequestParam UUID userId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws UserNotFoundException {
        return ResponseEntity.ok(documentService.getAllDocumentsByUserId(userId, pageNo, pageSize));
    }

    @GetMapping("/download-document")
    public ResponseEntity<ByteArrayResource> downloadDocument(@RequestParam UUID documentId) throws DocumentNotFoundException {
        DocumentResponse documentResponse = documentService.downloadDocument(documentId);

        ByteArrayResource resource = new ByteArrayResource(documentResponse.documentDataBase64());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + documentResponse.documentName() + "\"")
                .body(resource);
    }

    //POST
    @PostMapping("/upload-document")
    public ResponseEntity<DocumentResponse> uploadDocument(@RequestPart("file") MultipartFile file, @RequestParam String apartmentSignature, @RequestParam DocumentType documentType) throws IllegalArgumentException, IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file, apartmentSignature, documentType));
    }

    //DELETE
    @DeleteMapping("/delete-document")
    public ResponseEntity<String> deleteDocument(@RequestBody DocumentDeleteRequest documentDeleteRequest) throws DocumentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(documentService.deleteDocument(documentDeleteRequest.getDocumentId(), documentDeleteRequest.getUserId(), Boolean.valueOf(documentDeleteRequest.getDeleteCompletely())));
    }

}