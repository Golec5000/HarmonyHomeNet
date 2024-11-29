package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ProblemReportNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ProblemReportResponse;

public interface ProblemReportService {

    ProblemReportResponse createProblemReport(ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException;

    ProblemReportResponse updateProblemReport(Long problemReportId, ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException;

    String deleteProblemReport(Long problemReportId) throws ProblemReportNotFoundException;

    PageResponse<ProblemReportResponse> getProblemReportsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

    PageResponse<ProblemReportResponse> getAllProblemReports(int pageNo, int pageSize);


}
