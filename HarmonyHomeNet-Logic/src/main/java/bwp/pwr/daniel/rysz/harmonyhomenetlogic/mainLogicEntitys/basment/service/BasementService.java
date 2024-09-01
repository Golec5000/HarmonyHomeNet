package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BasementNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;

import java.util.List;
import java.util.UUID;

public interface BasementService {
    void deleteById(UUID id) throws BasementNotFoundException;

    BasementResponse findById(UUID id) throws BasementNotFoundException;

    BasementResponse findByBasementNumber(int basementNumber) throws BasementNotFoundException;

    List<BasementResponse> findAll();

    BasementResponse save(Basement basement);
}
