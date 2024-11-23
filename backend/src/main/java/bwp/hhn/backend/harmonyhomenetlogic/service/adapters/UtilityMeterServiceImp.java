package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UtilityMeterServiceImp implements UtilityMeterService {

    private static final Logger LOG = Logger.getLogger(UtilityMeterServiceImp.class.getName());

    private final Random random = new Random();

    private double waterMeterValue;

    private double electricityMeterValue;

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void generateRandomMeterReadings() {
        LOG.info("Generating random meter readings");
        waterMeterValue = random.nextDouble() * 1000;
        electricityMeterValue = random.nextDouble() * 1000;
    }

    @Override
    public String getWaterMeterValue(String utilityMeterId) {
        LOG.info("Getting water meter value for utility meter with id: " + utilityMeterId);
        return String.valueOf(waterMeterValue);
    }

    @Override
    public String getElectricityMeterValue(String utilityMeterId) {
        LOG.info("Getting electricity meter value for utility meter with id: " + utilityMeterId);
        return String.valueOf(electricityMeterValue);
    }
}
