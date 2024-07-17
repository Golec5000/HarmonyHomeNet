package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Owner;

import java.util.Optional;
import java.util.UUID;

public interface OwnerService {
    Optional<Owner> getOwnerById(UUID id);
    Owner saveOwner(Owner owner);
    void deleteOwner(UUID id);
    Optional<Owner> findOwnerByFirstNameAndLastName(String firstName, String lastName);
}
