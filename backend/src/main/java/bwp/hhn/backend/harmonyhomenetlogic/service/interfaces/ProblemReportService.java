package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ProblemReportNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ProblemReportResponse;

import java.util.UUID;

public interface ProblemReportService {

    ProblemReportResponse createProblemReport(ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException;

    ProblemReportResponse updateProblemReport(Long problemReportId, ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException;

    String deleteProblemReport(Long problemReportId) throws ProblemReportNotFoundException;

    ProblemReportResponse getProblemReportById(Long problemReportId) throws ProblemReportNotFoundException;

    PageResponse<ProblemReportResponse> getProblemReportsByUserId(UUID userId, int pageNo, int pageSize) throws UserNotFoundException;

    PageResponse<ProblemReportResponse> getProblemReportsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

    PageResponse<ProblemReportResponse> getAllProblemReports(int pageNo, int pageSize);

    PageResponse<ProblemReportResponse> getProblemReportsByStatus(ReportStatus status, int pageNo, int pageSize);


}
