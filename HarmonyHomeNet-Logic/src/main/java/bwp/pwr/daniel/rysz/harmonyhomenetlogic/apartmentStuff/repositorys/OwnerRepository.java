package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    Optional<Owner> findOwnerByFirstNameAndLastName(String firstName, String lastName);
}
