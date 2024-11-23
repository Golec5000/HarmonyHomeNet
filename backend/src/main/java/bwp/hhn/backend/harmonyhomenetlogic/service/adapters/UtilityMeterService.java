package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import java.util.UUID;

public interface UtilityMeterService {

    String getWaterMeterValue(String utilityMeterId);

    String getElectricityMeterValue(String utilityMeterId);

}
