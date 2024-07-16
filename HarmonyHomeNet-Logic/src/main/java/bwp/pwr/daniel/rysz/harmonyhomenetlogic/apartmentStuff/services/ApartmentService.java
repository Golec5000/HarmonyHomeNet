package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Apartment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApartmentService {
    List<Apartment> findAll();
    Optional<Apartment> findById(UUID id);
    void save(Apartment apartment);
    void deleteById(UUID id);
    Optional<Apartment> findByApartmentNumber(int apartmentNumber);
}
