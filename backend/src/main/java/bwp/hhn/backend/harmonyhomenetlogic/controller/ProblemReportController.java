package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ProblemReportService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ProblemReportResponse;
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

    @PostMapping("/create-report")
    public ResponseEntity<ProblemReportResponse> createProblemReport(@RequestBody ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException {
        return ResponseEntity.ok(problemReportService.createProblemReport(problemReportRequest));
    }

    @PutMapping("/update-report/{problemReportId}")
    public ResponseEntity<ProblemReportResponse> updateProblemReport(@PathVariable Long problemReportId, @RequestBody ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.updateProblemReport(problemReportId, problemReportRequest));
    }

    @DeleteMapping("/delete-report/{problemReportId}")
    public ResponseEntity<String> deleteProblemReport(@PathVariable Long problemReportId) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.deleteProblemReport(problemReportId));
    }

    @GetMapping("/get-report-by-id/{problemReportId}")
    public ResponseEntity<ProblemReportResponse> getProblemReportById(@PathVariable Long problemReportId) throws ProblemReportNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportById(problemReportId));
    }

    @GetMapping("/get-report-by-user/{userId}")
    public ResponseEntity<List<ProblemReportResponse>> getProblemReportsByUserId(@PathVariable UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportsByUserId(userId));
    }

    @GetMapping("/get-report-by-apartment/{apartmentSignature}")
    public ResponseEntity<List<ProblemReportResponse>> getProblemReportsByApartmentId(@PathVariable String apartmentSignature) throws ApartmentNotFoundException {
        return ResponseEntity.ok(problemReportService.getProblemReportsByApartmentSignature(apartmentSignature));
    }

    @GetMapping("/get-all-reports")
    public ResponseEntity<List<ProblemReportResponse>> getAllProblemReports() {
        return ResponseEntity.ok(problemReportService.getAllProblemReports());
    }

    @GetMapping("/get-reposts-by-status")
    public ResponseEntity<List<ProblemReportResponse>> getProblemReportsByStatus(@RequestParam ReportStatus status) {
        return ResponseEntity.ok(problemReportService.getProblemReportsByStatus(status));
    }
}