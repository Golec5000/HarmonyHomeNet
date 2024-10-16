package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Posts", indexes = {
        @Index(name = "idx_post_created_at", columnList = "Created_at"),
        @Index(name = "idx_post_topic_id", columnList = "topic_id"),
        @Index(name = "idx_post_users_id", columnList = "users_id")
})
public class Post {

    @Id
    @Column(name = "UUID_id")
    private UUID uuidID;

    @NotEmpty
    @Size(max = 1000)
    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "UUID_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "UUID_id")
    private User user;

}