package bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PossessionHistoryRepository extends JpaRepository<PossessionHistory, Long> {
    // Pobranie aktualnych mieszkańców danego apartamentu
    @Query("SELECT ph.user FROM PossessionHistory ph WHERE ph.apartment.apartmentSignature = :apartmentSignature AND ph.endDate IS NULL")
    Page<User> findActiveResidentsByApartment(String apartmentSignature, Pageable pageable);

    @Query("SELECT ph.user FROM PossessionHistory ph WHERE ph.apartment.apartmentSignature = :apartmentSignature AND ph.endDate IS NULL")
    List<User> findActiveResidentsByApartment(String apartmentSignature);

    boolean existsByUserUuidIDAndApartmentUuidID(UUID userId, UUID apartmentId);

    Page<Apartment> findByUserUuidID (UUID userId, Pageable pageable);

    Optional<PossessionHistory> findByUserUuidIDAndApartmentUuidID (UUID userId, UUID apartmentId);

    Page<PossessionHistory> findByApartmentUuidID (UUID apartmentId, Pageable pageable);

    Page<Apartment> findByUserUuidIDAndEndDateIsNull(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT ph.user FROM PossessionHistory ph")
    List<User> findAllUniqueOwners();

    String findApartmentSignatureByUserUuidID(UUID userId);
}