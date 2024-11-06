package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Vote_choice", nullable = false)
    private VoteChoice voteChoice;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Apartment_signature")
    private String apartmentSignature;

    @ManyToOne
    @JoinColumn(name = "poll_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

}