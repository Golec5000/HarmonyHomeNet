package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Problem_reports", indexes = {
        @Index(name = "idx_problem_filing_date", columnList = "End_date"),
        @Index(name = "idx_problem_status", columnList = "status"),
        @Index(name = "idx_problem_user_id", columnList = "user_id"),
        @Index(name = "idx_problem_apartment_id", columnList = "apartment_id"),
        @Index(name = "idx_problem_create_at", columnList = "Created_at")
})
public class ProblemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ReportStatus reportStatus;

    @NotEmpty
    @Size(max = 1000)
    @Column(name = "Note", length = 1000, nullable = false)
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Category", nullable = false)
    private Category category;

    @Column(name = "End_date")
    private Instant endDate;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Apartment apartment;
}