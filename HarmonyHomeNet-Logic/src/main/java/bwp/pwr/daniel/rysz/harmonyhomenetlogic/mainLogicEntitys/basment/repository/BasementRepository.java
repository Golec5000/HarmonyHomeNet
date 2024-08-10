package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasementRepository extends JpaRepository<Basement, UUID> {
    Optional<Basement> findByBasementNumber(int basementNumber);
}
