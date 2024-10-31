package bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Document;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_documents_connections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"documents_id", "users_id"})
})
public class UserDocumentConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuidID;

    @ManyToOne
    @JoinColumn(name = "documents_id", nullable = false)
    private Document document;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

}
