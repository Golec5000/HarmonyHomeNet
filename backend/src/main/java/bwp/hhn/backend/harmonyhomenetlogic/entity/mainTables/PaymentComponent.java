package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "Payment_components", indexes = {
        @Index(name = "idx_paymentcomponent", columnList = "payment_id")
})
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
    @JoinColumn(name = "payment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Payment payment;

}
