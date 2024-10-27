package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UUID_id")
    private UUID uuidID;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "Address", nullable = false, length = 50)
    private String address;

    @NotEmpty
    @Size(max = 20)
    @Column(name = "City", nullable = false, length = 20)
    private String city;

    @NotEmpty
    @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Invalid zip code format")
    @Column(name = "Zip_code", nullable = false, length = 6)
    private String zipCode;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    @Column(name = "Apartment_area", nullable = false, precision = 5, scale = 2)
    private BigDecimal apartmentArea;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    @Column(name = "Apartment_percent_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal apartmentPercentValue;

    @CreationTimestamp
    @Column(name = "Create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "Update_at", updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PossessionHistory> possessionHistories;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProblemReport> problemReports;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Payment> payments;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AnnouncementApartment> announcementApartments;
}
