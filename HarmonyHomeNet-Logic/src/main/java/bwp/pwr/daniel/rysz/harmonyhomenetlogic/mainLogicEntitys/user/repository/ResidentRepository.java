package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, UUID> {
}
