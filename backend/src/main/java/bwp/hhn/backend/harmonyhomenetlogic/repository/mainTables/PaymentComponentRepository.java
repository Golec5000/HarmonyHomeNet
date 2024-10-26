package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.PaymentComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentComponentRepository extends JpaRepository<PaymentComponent, Long> {

    List<PaymentComponent> findAllByPaymentUuidID(UUID paymentId);

}