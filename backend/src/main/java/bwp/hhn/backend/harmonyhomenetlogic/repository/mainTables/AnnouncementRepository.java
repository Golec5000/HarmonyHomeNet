package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a LEFT JOIN FETCH a.announcementApartments WHERE a.id = :id")
    Optional<Announcement> findByIdWithApartments(@Param("id") Long id);

}