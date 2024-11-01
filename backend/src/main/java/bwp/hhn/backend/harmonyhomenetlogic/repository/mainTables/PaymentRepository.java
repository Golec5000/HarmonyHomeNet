package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByApartmentUuidID(UUID apartmentId);

    List<Payment> findByPaymentDateBetween(LocalDateTime twoWeeksBefore, LocalDateTime oneWeekBefore);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate < :today AND p.paymentStatus = 'NOT_PAID'")
    List<Payment> findByDueDateBeforeAndIsPaidFalse(@Param("today") LocalDateTime today);

}