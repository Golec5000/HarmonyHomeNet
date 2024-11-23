package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bwp/hhn/api/v1/user/utility-meter")
public class UtilityMeterController {

    private final UtilityMeterService utilityMeterService;

    @GetMapping("/water-meter/{id}")
    public String getWaterMeterValue(@PathVariable String id) {
        return utilityMeterService.getWaterMeterValue(id);
    }

    @GetMapping("/electricity-meter/{id}")
    public String getElectricityMeterValue(@PathVariable String id) {
        return utilityMeterService.getElectricityMeterValue(id);
    }
}