package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "Component_type", nullable = false, length = 50)
    private String componentType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "Unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "1.0", message = "Special multiplier must be greater than 1")
    @Column(name = "Special_multiplier", precision = 10, scale = 2)
    private BigDecimal specialMultiplier;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "Component_amount", precision = 10, scale = 2)
    private BigDecimal componentAmount;

    @NotNull
    @Column(name = "Unit", nullable = false)
    private String unit;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Payment payment;
}