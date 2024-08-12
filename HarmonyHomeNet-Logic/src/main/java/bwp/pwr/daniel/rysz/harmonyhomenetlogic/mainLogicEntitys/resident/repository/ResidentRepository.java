package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, UUID> {
    Optional<Resident> findResidentByLogin(String login);

    Optional<Resident> findResidentByPESELNumber(String PESELNumber);
}
