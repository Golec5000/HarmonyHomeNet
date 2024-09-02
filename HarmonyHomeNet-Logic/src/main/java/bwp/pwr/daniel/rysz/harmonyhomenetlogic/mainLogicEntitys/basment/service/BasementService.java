package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BasementNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface BasementService {
    void deleteById(UUID id) throws BasementNotFoundException;

    Basement findById(UUID id) throws BasementNotFoundException;

    Basement findByBasementNumber(int basementNumber) throws BasementNotFoundException;

    List<Basement> findAll();

    BasementResponse save(@NonNull Basement basement);

    BasementResponse mapBasementToBasementResponse(@NonNull Basement basement);

    List<BasementResponse> mapBasementListToBasementResponseList(@NonNull List<Basement> basementList);
}
