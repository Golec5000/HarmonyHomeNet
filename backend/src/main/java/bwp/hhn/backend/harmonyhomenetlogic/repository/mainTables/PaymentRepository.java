package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findAllByApartmentUuidID(UUID apartmentId, Pageable pageable);

    List<Payment> findByPaymentDateBetween(Instant twoWeeksBefore, Instant oneWeekBefore);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate < :today AND p.paymentStatus = 'NOT_PAID'")
    List<Payment> findByDueDateBeforeAndIsPaidFalse(@Param("today") Instant today);

}