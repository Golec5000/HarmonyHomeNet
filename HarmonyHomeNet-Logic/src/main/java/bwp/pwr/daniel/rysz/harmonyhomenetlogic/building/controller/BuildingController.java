package bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.service.BuildingService;
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

    @GetMapping("/all")
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.findAll());
    }

}
