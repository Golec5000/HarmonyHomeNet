package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Apartments", indexes = {
        @Index(name = "idx_apartments_address", columnList = "Address")
})
public class Apartments {

    @Id
    @Column(name = "UUID_id")
    private UUID uuidID;

    @Column(name = "Address", nullable = false, length = 50)
    private String address;

    @Column(name = "City", nullable = false, length = 20)
    private String city;

    @Column(name = "Zip_code", nullable = false, length = 6)
    private String zipCode;

    @Column(name = "Apartment_area", nullable = false, precision = 5, scale = 2)
    private BigDecimal apartmentArea;

    @Column(name = "Apartment_percent_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal apartmentPercentValue;

    @CreationTimestamp
    @Column(name = "Create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "Update_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<PossessionHistory> possessionHistories;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<ProblemReport> problemReports;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<AnnouncementApartment> announcementApartments;
}
