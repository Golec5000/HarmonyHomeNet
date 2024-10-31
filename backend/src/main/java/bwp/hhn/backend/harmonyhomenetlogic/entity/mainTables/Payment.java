package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "Payments", indexes = {
        @Index(name = "idx_payment_apartment_id", columnList = "apartment_id"),
        @Index(name = "idx_payment_date", columnList = "Payment_date"),
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UUID_id")
    private UUID uuidID;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Payment_status", nullable = false, length = 8)
    private PaymentStatus paymentStatus;

    @NotNull
    @Column(name = "Payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "Payment_time")
    private LocalDateTime paymentTime;

    @DecimalMin(value = "0.0")
    @Column(name = "Payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Apartment apartment;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PaymentComponent> paymentComponents;
}