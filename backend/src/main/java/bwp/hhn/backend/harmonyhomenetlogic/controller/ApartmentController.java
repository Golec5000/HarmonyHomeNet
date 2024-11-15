package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ApartmentsService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ApartmentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ApartmentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PossessionHistoryResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/apartment")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentsService apartmentsService;

    // GET
    @GetMapping("/get-apartment-by-signature")
    public ResponseEntity<ApartmentResponse> getApartmentById(@RequestParam String apartmentSignature) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getApartmentBySignature(apartmentSignature));
    }

    @GetMapping("/get-all-apartments")
    public ResponseEntity<PageResponse<ApartmentResponse>> getAllApartments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(apartmentsService.getAllApartments(pageNo, pageSize));
    }

    @GetMapping("/possession-history-for-apartment")
    public ResponseEntity<PossessionHistoryResponse> getPossessionHistory(@RequestParam String apartmentSignature, @RequestParam UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.getPossessionHistory(apartmentSignature, userId));
    }

    @GetMapping("/current-apartment-residents")
    public ResponseEntity<List<UserResponse>> getCurrentResidents(@RequestParam String apartmentSignature)
            throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getCurrentResidents(apartmentSignature));
    }

    @GetMapping("/whole-possession-history-for-apartment")
    public ResponseEntity<PageResponse<PossessionHistoryResponse>> getApartmentPossessionHistory(
            @RequestParam String apartmentSignature,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getApartmentPossessionHistory(apartmentSignature, pageNo, pageSize));
    }

    @GetMapping("/apartment-residents")
    public ResponseEntity<PageResponse<UserResponse>> getAllResidentsByApartmentId(
            @RequestParam String apartmentSignature,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.getAllResidentsByApartmentId(apartmentSignature, pageNo, pageSize));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE') or hasRole('ROLE_OWNER')")
    @GetMapping("/get-all-user-apartments")
    public ResponseEntity<List<ApartmentResponse>> getAllUserApartments(@RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.getAllUserApartments(userId));
    }

    // POST
    @PostMapping("/create-possession-history")
    public ResponseEntity<PossessionHistoryResponse> createPossessionHistory(@RequestParam String apartmentSignature, @RequestParam UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.createPossessionHistory(apartmentSignature, userId));
    }

    @PostMapping("/create-apartment")
    public ResponseEntity<ApartmentResponse> createApartment(@RequestBody ApartmentRequest request) {
        return ResponseEntity.ok(apartmentsService.createApartments(request));
    }

    // DELETE
    @DeleteMapping("/delete-apartment")
    public ResponseEntity<String> deleteApartment(@RequestParam String apartmentSignature) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.deleteApartment(apartmentSignature));
    }

    @DeleteMapping("/delete-possession-history")
    public ResponseEntity<String> deletePossessionHistory(@RequestParam String apartmentSignature, @RequestParam UUID userId) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.deletePossessionHistory(apartmentSignature, userId));
    }

    // PUT
    @PutMapping("/update-apartment")
    public ResponseEntity<ApartmentResponse> updateApartment(@RequestParam String apartmentSignature, @RequestBody ApartmentRequest request) throws ApartmentNotFoundException {
        return ResponseEntity.ok(apartmentsService.updateApartment(request, apartmentSignature));
    }

    @PutMapping("/end-possession-history")
    public ResponseEntity<PossessionHistoryResponse> endPossessionHistory(@RequestParam String apartmentSignature, @RequestParam UUID userId) throws ApartmentNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(apartmentsService.endPossessionHistory(apartmentSignature, userId));
    }

}