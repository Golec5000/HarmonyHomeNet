package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ParkingSpaceResponse(
        UUID id,
        int number
) {
}
