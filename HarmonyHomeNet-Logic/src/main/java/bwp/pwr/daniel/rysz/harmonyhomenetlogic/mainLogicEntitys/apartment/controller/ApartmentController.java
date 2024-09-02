package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services.ApartmentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/api/v1/apartment")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService apartmentService;

    @GetMapping("/all")
    public ResponseEntity<List<ApartmentResponse>> getAllApartments() {
        List<ApartmentResponse> apartments = apartmentService.mapApartmentListToApartmentResponseList(apartmentService.findAll());
        return apartments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(apartments);
    }

    @GetMapping("/apartment-by-id/{apartment_id}")
    public ResponseEntity<ApartmentResponse> getApartmentById(@PathVariable String apartment_id) {
        return ResponseEntity.ok(apartmentService.mapApartmentToApartmentResponse(apartmentService.findById(UUID.fromString(apartment_id))));
    }

    @GetMapping("/apartment-by-number/{apartment_number}/building/{building_id}")
    public ResponseEntity<ApartmentResponse> getApartmentByNumberAndBuildingId(@PathVariable int apartment_number, @PathVariable String building_id) {
        return ResponseEntity.ok(apartmentService.mapApartmentToApartmentResponse(apartmentService.findByApartmentNumber(apartment_number, UUID.fromString(building_id))));
    }

    @PutMapping("/apartment-id/{apartment_id}/add-resident-tenant/{user_id}")
    public ResponseEntity<UserResponse> addResidentTenantToApartment(@PathVariable String apartment_id, @PathVariable String user_id) {
        return ResponseEntity.ok(apartmentService.addResidentTenantToApartment(UUID.fromString(user_id), UUID.fromString(apartment_id)));
    }

    @PutMapping("/apartment-id/{apartment_id}/add-resident-owner/{user_id}")
    public ResponseEntity<UserResponse> addResidentOwnerToApartment(@PathVariable String apartment_id, @PathVariable String user_id) {
        return ResponseEntity.ok(apartmentService.addResidentOwnerToApartment(UUID.fromString(user_id), UUID.fromString(apartment_id)));
    }

}
