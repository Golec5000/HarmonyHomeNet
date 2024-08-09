package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "data", nullable = false)
    @Lob
    private byte[] data;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "document_add_date", nullable = false)
    private LocalDateTime documentAddDate;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        documentAddDate = LocalDateTime.now();
        documentType = documentName.substring(documentName.lastIndexOf(".") + 1);
    }
}
