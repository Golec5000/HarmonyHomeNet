package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.DocumentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.entity.Document;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Document>> getAll() {
        return ResponseEntity.ok(documentService.findAll());
    }

    @GetMapping("/document-id/{documentId}")
    public ResponseEntity<byte[]> getDocumentById(@PathVariable String documentId) throws DocumentNotFoundException {
        UUID id = UUID.fromString(documentId);
        Document document = documentService.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("wrong document id"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getDocumentName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(document.getData());
    }

    @PostMapping("/upload-file")
    public ResponseEntity<Document> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        documentService.save(createDocumentEntity(file));
        return ResponseEntity.ok().build();
    }


    private Document createDocumentEntity(MultipartFile file) throws IOException {
        return Document.builder()
                .documentName(file.getOriginalFilename())
                .data(file.getBytes())
                .build();
    }
}