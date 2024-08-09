package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResidentRepository extends JpaRepository<Resident, UUID> {
    Optional<Resident> findResidentByLogin(String login);

    Optional<Resident> findResidentByPESELNumber(String PESELNumber);
}
