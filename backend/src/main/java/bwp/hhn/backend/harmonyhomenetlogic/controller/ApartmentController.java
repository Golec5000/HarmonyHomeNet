package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ApartmentsService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/apartment")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentsService apartmentsService;

    @PostMapping("/create-apartment")
    public ResponseEntity<ApartmentResponse> createApartment(@RequestBody ApartmentRequest request) {
        return ResponseEntity.ok(apartmentsService.createApartments(request));
    }

    @PutMapping("/update-apartment/{apartmentId}")
    public ResponseEntity<ApartmentResponse> updateApartment(@PathVariable UUID apartmentId, @RequestBody ApartmentRequest request) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.updateApartment(request, apartmentId));
    }

    @DeleteMapping("/delete-apartment/{apartmentId}")
    public ResponseEntity<String> deleteApartment(@PathVariable UUID apartmentId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.deleteApartment(apartmentId));
    }

    @GetMapping("/get-apartment-by-id/{apartmentId}")
    public ResponseEntity<ApartmentResponse> getApartmentById(@PathVariable UUID apartmentId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getApartmentById(apartmentId));
    }

    @GetMapping("/get-apartment-by-user/{userId}")
    public ResponseEntity<List<ApartmentResponse>> getApartmentsByUserId(@PathVariable UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.getApartmentsByUserId(userId));
    }

    @GetMapping("/get-all-apartments")
    public ResponseEntity<List<ApartmentResponse>> getAllApartments() {
        return ResponseEntity.ok(apartmentsService.getAllApartments());
    }

    @GetMapping("/possession-history-for-apartment/{apartmentId}/{userId}")
    public ResponseEntity<PossessionHistoryResponse> getPossessionHistory(@PathVariable UUID apartmentId, @PathVariable UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.getPossessionHistory(apartmentId, userId));
    }

    @PostMapping("/create-possession-history/{apartmentId}/{userId}")
    public ResponseEntity<PossessionHistoryResponse> createPossessionHistory(@PathVariable UUID apartmentId, @PathVariable UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.createPossessionHistory(apartmentId, userId));
    }

    @DeleteMapping("/delete-possession-history/{apartmentId}/{userId}")
    public ResponseEntity<PossessionHistoryResponse> deletePossessionHistory(@PathVariable UUID apartmentId, @PathVariable UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.deletePossessionHistory(apartmentId, userId));
    }

    @GetMapping("/current-apartment-residents/{apartmentId}")
    public ResponseEntity<List<UserResponse>> getCurrentResidents(@PathVariable UUID apartmentId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getCurrentResidents(apartmentId));
    }

    @GetMapping("/hole-possession-history-for-apartment/{apartmentId}")
    public ResponseEntity<List<PossessionHistoryResponse>> getApartmentPossessionHistory(@PathVariable UUID apartmentId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getApartmentPossessionHistory(apartmentId));
    }

    @GetMapping("/user-apartments/{userId}")
    public ResponseEntity<List<ApartmentResponse>> getAllUserApartments(@PathVariable UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.getAllUserApartments(userId));
    }

    @GetMapping("/apartment-residents/{apartmentId}")
    public ResponseEntity<List<UserResponse>> getAllResidentsByApartmentId(@PathVariable UUID apartmentId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getAllResidentsByApartmentId(apartmentId));
    }
}