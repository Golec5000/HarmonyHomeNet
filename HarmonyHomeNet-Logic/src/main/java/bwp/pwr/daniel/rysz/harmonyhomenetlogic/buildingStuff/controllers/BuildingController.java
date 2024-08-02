package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.controllers;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/v1/all")
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.findAll());
    }

}
