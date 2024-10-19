package bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDocumentConnectionRepository extends JpaRepository<UserDocumentConnection, UUID> {

    @Query("SELECT udp.document FROM UserDocumentConnection udp WHERE udp.user.uuidID = :userId")
    List<Document> findDocumentsByUserId(UUID userId);

    Optional<UserDocumentConnection> findByDocumentUuidIDAndUserUuidID(UUID documentId, UUID userId);

}