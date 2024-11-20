package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UtilityMeterServiceImp implements UtilityMeterService {

    private static final Logger LOG = Logger.getLogger(UtilityMeterServiceImp.class.getName());

    //todo shedile co miesiąc losowa wartośc

    @Override
    public String getWaterMeterValue(UUID utilityMeterId) {
        LOG.info("Getting water meter value for utility meter with id: " + utilityMeterId);
        return "Water meter value for utility meter with id: " + utilityMeterId + " is 123";
    }

    @Override
    public String getElectricityMeterValue(UUID utilityMeterId) {
        LOG.info("Getting electricity meter value for utility meter with id: " + utilityMeterId);
        return "Electricity meter value for utility meter with id: " + utilityMeterId + " is 456";
    }
}
