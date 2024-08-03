package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entitys.Resident;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResidentService {

    List<Resident> findAll();

    Optional<Resident> findById(UUID id);

    Optional<Resident> findByLogin(String login);

    void save(Resident resident);

    void deleteById(UUID id);

    Optional<Resident> findByPESELNumber(String PESELNumber);

}
