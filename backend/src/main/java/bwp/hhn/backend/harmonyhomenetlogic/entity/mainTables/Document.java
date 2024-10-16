package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentPermission;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Documents")
public class Document {

    @Id
    @Column(name = "UUID_id")
    private String uuidID;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "Document_name", nullable = false, length = 50)
    private String documentName;

    @NotEmpty
    @Size(max = 10)
    @Column(name = "Document_type", nullable = false, length = 10)
    private String documentType;

    @Lob
    @NotNull
    @Column(name = "Document_data", nullable = false)
    private byte[] documentData;

    @NotNull
    @Min(0)
    @Column(name = "Document_size", nullable = false)
    private long documentSize;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<UserDocumentPermission> userDocumentPermissions;
}