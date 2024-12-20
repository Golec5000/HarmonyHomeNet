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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @NotEmpty
    @Size(max = 8)
    @Column(name = "File_extension", nullable = false, length = 8)
    private String fileExtension;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "File_name", nullable = false, length = 100)
    private String fileName;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "End_date")
    private Instant endDate;

    @Column(name = "Current_votes_count", nullable = false)
    private int currentVotesCount;

    @NotNull
    @DecimalMin(value = "0.0")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "Summary", precision = 5, scale = 2)
    private BigDecimal summary;

    @NotNull
    @Column(name = "Min_current_votes_count", nullable = false)
    private int minCurrentVotesCount;

    @NotNull
    @DecimalMin(value = "0.0")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "Min_summary", precision = 5, scale = 2)
    private BigDecimal minSummary;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Vote> votes = new ArrayList<>();

    @Column(name = "notification_sent", nullable = false)
    private boolean notificationSent = false;

}