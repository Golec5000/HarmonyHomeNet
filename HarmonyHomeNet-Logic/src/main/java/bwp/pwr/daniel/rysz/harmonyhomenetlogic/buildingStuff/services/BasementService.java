package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Basement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasementService {
    void deleteById(UUID id);
    Optional<Basement> findById(UUID id);
    Optional<Basement> findByBasementNumber(int basementNumber);
    List<Basement> findAll();
    void save(Basement basement);
}
