package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Polls", indexes = {
        @Index(name = "idx_poll_user_id", columnList = "users_id"),
        @Index(name = "idx_poll_created_at", columnList = "created_at")
})
public class Poll {

    @Id
    @Column(name = "UUID_id")
    private UUID uuidID;

    @Column(name = "Poll_name", nullable = false)
    private String pollName;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Lob
    @Column(name = "Upload_data", nullable = false)
    private byte[] uploadData;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "End_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;
}
