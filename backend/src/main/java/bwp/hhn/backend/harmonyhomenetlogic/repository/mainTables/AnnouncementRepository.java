package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByUserUuidID(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Announcement a WHERE a.createdAt BETWEEN :startDate AND :endDate OR a.updatedAt BETWEEN :startDate AND :endDate")
    Page<Announcement> findDistinctByCreatedAtOrUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT a FROM Announcement a LEFT JOIN FETCH a.announcementApartments WHERE a.id = :id")
    Optional<Announcement> findByIdWithApartments(@Param("id") Long id);

}