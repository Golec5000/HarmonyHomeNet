package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import java.util.UUID;

public interface UtilityMeterService {

    String getWaterMeterValue(UUID utilityMeterId);

    String getElectricityMeterValue(UUID utilityMeterId);

}
