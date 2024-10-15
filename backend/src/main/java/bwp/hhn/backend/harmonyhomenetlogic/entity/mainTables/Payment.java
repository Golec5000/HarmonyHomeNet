package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "UUID_id")
    private UUID uuidID;

    @Enumerated(EnumType.STRING)
    @Column(name = "Payment_status", nullable = false, length = 8)
    private PaymentStatus paymentStatus;

    @Column(name = "Payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "Payment_time")
    private LocalDateTime paymentTime;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Apartments apartment;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PaymentComponent> paymentComponents;
}
