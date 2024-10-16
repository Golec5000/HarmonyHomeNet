package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

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

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Special_multiplier", precision = 10, scale = 2)
    private BigDecimal specialMultiplier;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Component_amount", precision = 10, scale = 2)
    private BigDecimal componentAmount;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Payment payment;
}