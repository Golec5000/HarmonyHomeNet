package bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.utils.AccessLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "User_documents_permissions")
public class UserDocumentPermission {

    @Id
    @Column(name = "UUID_id")
    private String uuidID;

    @Enumerated(EnumType.STRING)
    @Column(name = "Access_level", nullable = false, length = 50)
    private AccessLevel accessLevel;

    @Column(name = "Granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @Column(name = "Revoked_at")
    private LocalDateTime revokedAt;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "documents_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Document document;

}
