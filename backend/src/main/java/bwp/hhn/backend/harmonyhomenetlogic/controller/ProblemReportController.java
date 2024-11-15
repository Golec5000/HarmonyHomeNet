package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ProblemReportNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ProblemReportService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ProblemReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/problem-report")
@RequiredArgsConstructor
public class ProblemReportController {

    private final ProblemReportService problemReportService;

    //GET
    @GetMapping("/get-report-by-id/{problemReportId}")
    public ResponseEntity<ProblemReportResponse> getProblemReportById(@PathVariable Long problemReportId) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportById(problemReportId));
    }

    @GetMapping("/get-report-by-user/{userId}")
    public ResponseEntity<PageResponse<ProblemReportResponse>> getProblemReportsByUserId(
            @PathVariable UUID userId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws UserNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportsByUserId(userId, pageNo, pageSize));
    }

    @GetMapping("/get-report-by-apartment/{apartmentSignature}")
    public ResponseEntity<PageResponse<ProblemReportResponse>> getProblemReportsByApartmentId(
            @PathVariable String apartmentSignature,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws ApartmentNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportsByApartmentSignature(apartmentSignature, pageNo, pageSize));
    }

    @GetMapping("/get-all-reports")
    public ResponseEntity<PageResponse<ProblemReportResponse>> getAllProblemReports(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(problemReportService.getAllProblemReports(pageNo, pageSize));
    }

    @GetMapping("/get-reposts-by-status")
    public ResponseEntity<PageResponse<ProblemReportResponse>> getProblemReportsByStatus(
            @RequestParam ReportStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        return ResponseEntity.ok(problemReportService.getProblemReportsByStatus(status, pageNo, pageSize));
    }

    @GetMapping("/get-types-of-reports")
    public ResponseEntity<List<Category>> getTypesOfReports(){
        return ResponseEntity.ok(List.of(Category.values()));
    }

    //POST
    @PostMapping("/create-report")
    public ResponseEntity<ProblemReportResponse> createProblemReport(@RequestBody ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException {
        return ResponseEntity.ok(problemReportService.createProblemReport(problemReportRequest));
    }

    //PUT
    @PutMapping("/update-report/{problemReportId}")
    public ResponseEntity<ProblemReportResponse> updateProblemReport(@PathVariable Long problemReportId, @RequestBody ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.updateProblemReport(problemReportId, problemReportRequest));
    }

    //DELETE
    @DeleteMapping("/delete-report/{problemReportId}")
    public ResponseEntity<String> deleteProblemReport(@PathVariable Long problemReportId) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.deleteProblemReport(problemReportId));
    }

}