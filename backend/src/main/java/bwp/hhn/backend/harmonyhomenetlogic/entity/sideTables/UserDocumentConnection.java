package bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "User_documents_connections")
public class UserDocumentConnection {

    @Id
    @Column(name = "UUID_id")
    private UUID uuidID;

    @CreationTimestamp
    @Column(name = "Granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    @UpdateTimestamp
    @Column(name = "Updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "documents_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Document document;

}
