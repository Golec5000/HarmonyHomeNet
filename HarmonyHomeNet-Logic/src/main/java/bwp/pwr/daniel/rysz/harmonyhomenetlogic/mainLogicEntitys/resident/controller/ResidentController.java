package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/residents")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @GetMapping("/v1/all")
    public ResponseEntity<List<Resident>> findAll(){
        return ResponseEntity.ok(residentService.findAll());
    }

}
