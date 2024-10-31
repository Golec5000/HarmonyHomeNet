package bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AnnouncementApartmentRepository extends JpaRepository<AnnouncementApartment, Long> {

    @Query("SELECT aa FROM AnnouncementApartment aa JOIN aa.apartment a WHERE a.apartmentSignature = :apartmentSignature")
    List<AnnouncementApartment> findByApartmentSignature(String apartmentSignature);

    @Query("SELECT aa.apartment.uuidID FROM AnnouncementApartment aa WHERE aa.announcement.id = :announcementId")
    List<UUID> findApartmentIdsByAnnouncementId(@Param("announcementId") Long announcementId);

}