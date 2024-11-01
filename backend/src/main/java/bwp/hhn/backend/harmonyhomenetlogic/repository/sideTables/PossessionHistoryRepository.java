package bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PossessionHistoryRepository extends JpaRepository<PossessionHistory, Long> {
    // Pobranie aktualnych mieszkańców danego apartamentu
    @Query("SELECT ph.user FROM PossessionHistory ph WHERE ph.apartment.apartmentSignature = :apartmentSignature AND ph.endDate IS NULL")
    List<User> findActiveResidentsByApartment(String apartmentSignature);

    boolean existsByUserUuidIDAndApartmentUuidID(UUID userId, UUID apartmentId);

    List<PossessionHistory> findByUserUuidID (UUID userId);

    Optional<PossessionHistory> findByUserUuidIDAndApartmentUuidID (UUID userId, UUID apartmentId);

    List<PossessionHistory> findByApartmentUuidID (UUID apartmentId);

    List<PossessionHistory> findByUserUuidIDAndEndDateIsNull(UUID userId);

    @Query("SELECT DISTINCT ph.user FROM PossessionHistory ph")
    List<User> findAllUniqueOwners();

    String findApartmentSignatureByUserUuidID(UUID userId);
}