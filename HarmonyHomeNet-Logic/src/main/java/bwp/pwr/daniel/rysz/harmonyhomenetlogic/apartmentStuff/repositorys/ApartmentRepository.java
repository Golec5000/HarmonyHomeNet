package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {
    Optional<Apartment> findByApartmentNumber(int apartmentNumber);
}
