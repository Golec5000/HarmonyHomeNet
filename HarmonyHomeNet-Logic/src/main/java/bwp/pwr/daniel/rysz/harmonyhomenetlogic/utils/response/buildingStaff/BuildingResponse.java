package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BuildingResponse(
        UUID id,
        String buildingName,
        String street,
        String city,
        Region region
) {
}
