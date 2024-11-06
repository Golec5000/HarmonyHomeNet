package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    boolean existsByUuidID(UUID documentId);

    List<Document> findByDocumentTypeNot(DocumentType documentType);

    @Query("SELECT d FROM Document d JOIN UserDocumentConnection udc ON d.uuidID = udc.document.uuidID " +
            "WHERE udc.user.uuidID = :userId")
    Page<Document> findDocumentsByUserId(UUID userId, Pageable pageable);
}