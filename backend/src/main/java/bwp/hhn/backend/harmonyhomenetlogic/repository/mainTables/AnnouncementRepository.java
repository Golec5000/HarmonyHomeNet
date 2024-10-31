package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByUserUuidID(UUID userId);

    @Query("SELECT DISTINCT a FROM Announcement a WHERE a.createdAt BETWEEN :startDate AND :endDate OR a.updatedAt BETWEEN :startDate AND :endDate")
    List<Announcement> findDistinctByCreatedAtOrUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM Announcement a LEFT JOIN FETCH a.announcementApartments WHERE a.id = :id")
    Optional<Announcement> findByIdWithApartments(@Param("id") Long id);

}