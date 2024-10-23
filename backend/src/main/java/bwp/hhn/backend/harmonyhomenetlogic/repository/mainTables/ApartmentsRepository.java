package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApartmentsRepository extends JpaRepository<Apartment, UUID> {
    boolean existsByUuidID (UUID uuidID);
}