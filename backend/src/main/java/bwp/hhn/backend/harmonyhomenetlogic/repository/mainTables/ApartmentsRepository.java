package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApartmentsRepository extends JpaRepository<Apartments, UUID> {
}