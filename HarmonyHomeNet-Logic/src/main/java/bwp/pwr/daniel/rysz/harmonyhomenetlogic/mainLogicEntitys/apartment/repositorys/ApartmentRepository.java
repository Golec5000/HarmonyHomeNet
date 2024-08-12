package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {
    Optional<Apartment> findByApartmentNumber(int apartmentNumber);
}
