package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BuildingRequest {

    private String buildingName;

    private String street;

    private String city;

    private String region;
}
