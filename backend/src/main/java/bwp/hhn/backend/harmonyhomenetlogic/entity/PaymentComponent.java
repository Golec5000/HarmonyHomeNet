package bwp.hhn.backend.harmonyhomenetlogic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Payment_components")
public class PaymentComponent {

    @Id
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(name = "Component_type", nullable = false, length = 50)
    private String componentType;

    @Column(name = "Unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "Special_multiplier", precision = 10, scale = 2)
    private BigDecimal specialMultiplier;

    @Column(name = "Component_amout", precision = 10, scale = 2)
    private BigDecimal componentAmount;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

}
