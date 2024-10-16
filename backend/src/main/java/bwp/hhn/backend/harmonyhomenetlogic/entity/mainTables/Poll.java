package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
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

    @NotEmpty
    @Size(max = 100)
    @Column(name = "Poll_name", nullable = false, length = 100)
    private String pollName;

    @NotEmpty
    @Size(max = 1000)
    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Lob
    @NotNull
    @Column(name = "Upload_data", nullable = false)
    private byte[] uploadData;

    @CreationTimestamp
    @NotNull
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "End_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Vote> votes;
}