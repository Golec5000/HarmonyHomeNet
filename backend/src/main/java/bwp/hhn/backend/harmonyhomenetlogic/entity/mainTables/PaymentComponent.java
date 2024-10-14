package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(name = "ID")
    private Long id;

    @Column(name = "Component_type", nullable = false, length = 50)
    private String componentType;

    @Column(name = "Unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "Special_multiplier", precision = 10, scale = 2)
    private BigDecimal specialMultiplier;

    @Column(name = "Component_amount", precision = 10, scale = 2)
    private BigDecimal componentAmount;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @JsonBackReference
    private Payment payment;

}
