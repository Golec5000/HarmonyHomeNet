package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.ReportStatus;
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
@Table(name = "Problem_reports", indexes = {
        @Index(name = "idx_problem_filing_date", columnList = "filing_date"),
        @Index(name = "idx_problem_status", columnList = "status"),
        @Index(name = "idx_problem_user_id", columnList = "user_id"),
        @Index(name = "idx_problem_apartment_id", columnList = "apartment_id"),
        @Index(name = "idx_problem_create_at", columnList = "Created_at")
})
public class ProblemReport {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "Filing_date", nullable = false)
    private LocalDateTime filingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ReportStatus reportStatus;

    @Column(name = "Note", length = 1000, nullable = false)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "Category", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartments apartment;

}