package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.PaymentComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentComponentRepository extends JpaRepository<PaymentComponent, Long> {

    Page<PaymentComponent> findAllByPaymentUuidID(UUID paymentId, Pageable pageable);

}