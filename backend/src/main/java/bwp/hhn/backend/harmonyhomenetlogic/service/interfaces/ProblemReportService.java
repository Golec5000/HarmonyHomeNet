package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ProblemReportNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ProblemReportResponse;

import java.util.List;
import java.util.UUID;

public interface ProblemReportService {

    ProblemReportResponse createProblemReport(ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException;

    ProblemReportResponse updateProblemReport(Long problemReportId, ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException;

    String deleteProblemReport(Long problemReportId) throws ProblemReportNotFoundException;

    ProblemReportResponse getProblemReportById(Long problemReportId) throws ProblemReportNotFoundException;

    List<ProblemReportResponse> getProblemReportsByUserId(UUID userId) throws UserNotFoundException;

    List<ProblemReportResponse> getProblemReportsByApartmentSignature(String apartmentSignature) throws ApartmentNotFoundException;

    List<ProblemReportResponse> getAllProblemReports();

    List<ProblemReportResponse> getProblemReportsByStatus(ReportStatus status);


}
