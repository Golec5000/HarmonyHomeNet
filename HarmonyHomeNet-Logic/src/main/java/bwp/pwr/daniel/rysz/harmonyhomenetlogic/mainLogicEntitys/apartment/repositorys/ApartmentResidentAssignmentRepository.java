package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.ApartmentResidentAssignment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.ResourceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentResidentAssignmentRepository extends JpaRepository<ApartmentResidentAssignment, UUID> {

    Optional<ResourceRole> findByApartmentIdAndResidentId (UUID apartmentId, UUID residentId);

    boolean existsByApartmentIdAndResidentId(UUID apartmentId, UUID residentId);

}
