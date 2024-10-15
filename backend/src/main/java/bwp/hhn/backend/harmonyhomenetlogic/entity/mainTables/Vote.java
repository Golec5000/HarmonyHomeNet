package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.VoteChoice;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Votes", indexes = {
        @Index(name = "idx_vote_poll_id", columnList = "poll_id"),
        @Index(name = "idx_vote_user_id", columnList = "user_id")
})
public class Vote {

    @Id
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Vote_choice", nullable = false)
    private VoteChoice voteChoice;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "poll_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

}
