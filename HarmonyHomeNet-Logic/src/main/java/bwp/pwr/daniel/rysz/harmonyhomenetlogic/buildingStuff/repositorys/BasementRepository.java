package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Basement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BasementRepository extends JpaRepository<Basement, UUID> {
    Optional<Basement> findByBasementNumber(int basementNumber);
}
