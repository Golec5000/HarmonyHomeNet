package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.buildingStaff;


import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Region;

public record BuildingRequest (
        String buildingName,
        String street,
        String city,
        Region region
){
}
