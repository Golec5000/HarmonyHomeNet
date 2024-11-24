package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

public interface UtilityMeterService {

    String getWaterMeterValue(String utilityMeterId);

    String getElectricityMeterValue(String utilityMeterId);

}
