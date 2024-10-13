package bwp.hhn.backend.harmonyhomenetlogic.entity;

import bwp.hhn.backend.harmonyhomenetlogic.utils.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        @Index(name = "idx_problem_apartment_id", columnList = "apartment_id")
})
public class ProblemReport {

    @Id
    @Column(name = "ID", unique = true)
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartments apartment;

}