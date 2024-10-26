package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Payment_components", indexes = {
        @Index(name = "idx_paymentcomponent", columnList = "payment_id")
})
public class PaymentComponent {

    @Id
    @Column(name = "ID")
    private Long id;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "Component_type", nullable = false, length = 50)
    private String componentType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "1.0", inclusive = false, message = "Special multiplier must be greater than 1")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Special_multiplier", precision = 10, scale = 2)
    private BigDecimal specialMultiplier;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Component_amount", precision = 10, scale = 2)
    private BigDecimal componentAmount;

    @CreationTimestamp
    @Column(name = "Created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Payment payment;
}