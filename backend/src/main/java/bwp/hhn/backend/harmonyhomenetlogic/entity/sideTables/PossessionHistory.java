package bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Possession_history", indexes = {
        @Index(name = "idx_possession_user_id", columnList = "user_id"),
        @Index(name = "idx_possession_apartment_id", columnList = "apartment_id"),
        @Index(name = "idx_possession_start_date", columnList = "start_date"),
        @Index(name = "idx_possession_end_date", columnList = "end_date")
})
public class PossessionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @CreationTimestamp
    @Column(name = "Start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "End_date")
    private LocalDateTime endDate;

    @Column(name = "Status_notes", length = 1000)
    private String statusNotes;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Apartment apartment;
}