package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Topics", indexes = {
        @Index(name = "idx_topic_created_at", columnList = "Created_at"),
        @Index(name = "idx_topic_users_id", columnList = "users_id")
})
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UUID_id")
    private UUID uuidID;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<Post> posts;

}
