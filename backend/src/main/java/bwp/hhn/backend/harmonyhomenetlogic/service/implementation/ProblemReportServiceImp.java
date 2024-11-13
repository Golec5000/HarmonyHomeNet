package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ProblemReportNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.ProblemReport;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ProblemReportRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ProblemReportService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.ProblemReportResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemReportServiceImp implements ProblemReportService {

    private final ProblemReportRepository problemReportRepository;
    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final MailService mailService;


    @Override
    @Transactional
    public ProblemReportResponse createProblemReport(ProblemReportRequest problemReportRequest) throws UserNotFoundException, ApartmentNotFoundException {

        String apartmentSignature = problemReportRequest.getApartmentSignature();
        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found with id: " + apartmentSignature));

        UUID userId = problemReportRequest.getUserId();

        if (!possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID())) {
            throw new UserNotFoundException("User with id: " + userId + " does not have access to apartment with signature: " + apartmentSignature);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        ProblemReport newProblemReport = ProblemReport.builder()
                .note(problemReportRequest.getNote())
                .reportStatus(problemReportRequest.getReportStatus())
                .category(problemReportRequest.getCategory())
                .user(user)
                .apartment(apartment)
                .build();


        if (user.getProblemReports() == null) user.setProblemReports(new ArrayList<>());
        user.getProblemReports().add(newProblemReport);

        if (apartment.getProblemReports() == null) apartment.setProblemReports(new ArrayList<>());
        apartment.getProblemReports().add(newProblemReport);

        ProblemReport saved = problemReportRepository.save(newProblemReport);
        userRepository.save(user);
        apartmentsRepository.save(apartment);

        return ProblemReportResponse.builder()
                .id(saved.getId())
                .note(saved.getNote())
                .reportStatus(saved.getReportStatus())
                .category(saved.getCategory())
                .userName(user.getFirstName() + " " + user.getLastName())
                .apartmentAddress(apartment.getAddress())
                .build();
    }

    @Override
    public ProblemReportResponse updateProblemReport(Long problemReportId, ProblemReportRequest problemReportRequest) throws ProblemReportNotFoundException {

        ProblemReport problemReportToUpdate = problemReportRepository.findById(problemReportId)
                .orElseThrow(() -> new ProblemReportNotFoundException("Problem report not found with id: " + problemReportId));

        problemReportToUpdate.setNote(problemReportRequest.getNote() != null ? problemReportRequest.getNote() : problemReportToUpdate.getNote());
        problemReportToUpdate.setReportStatus(problemReportRequest.getReportStatus() != null ? problemReportRequest.getReportStatus() : problemReportToUpdate.getReportStatus());
        problemReportToUpdate.setCategory(problemReportRequest.getCategory() != null ? problemReportRequest.getCategory() : problemReportToUpdate.getCategory());

        if (ReportStatus.DONE.equals(problemReportRequest.getReportStatus()))
            problemReportToUpdate.setEndDate(Instant.now());

        mailService.sendNotificationMail("Problem report updated", "Your problem report has been updated", problemReportToUpdate.getUser().getEmail());

        ProblemReport updated = problemReportRepository.save(problemReportToUpdate);

        return ProblemReportResponse.builder()
                .id(updated.getId())
                .note(updated.getNote())
                .reportStatus(updated.getReportStatus())
                .category(updated.getCategory())
                .userName(updated.getUser().getFirstName() + " " + updated.getUser().getLastName())
                .apartmentAddress(updated.getApartment().getAddress())
                .endDate(updated.getEndDate())
                .build();

    }

    @Override
    public String deleteProblemReport(Long problemReportId) throws ProblemReportNotFoundException {

        if (!problemReportRepository.existsById(problemReportId)) {
            throw new ProblemReportNotFoundException("Problem report not found with id: " + problemReportId);
        }

        problemReportRepository.deleteById(problemReportId);

        return "Problem report deleted successfully";
    }

    @Override
    public ProblemReportResponse getProblemReportById(Long problemReportId) throws ProblemReportNotFoundException {

        return problemReportRepository.findById(problemReportId)
                .map(problemReport -> ProblemReportResponse.builder()
                        .id(problemReport.getId())
                        .note(problemReport.getNote())
                        .reportStatus(problemReport.getReportStatus())
                        .category(problemReport.getCategory())
                        .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                        .apartmentAddress(problemReport.getApartment().getAddress())
                        .endDate(problemReport.getEndDate())
                        .build())
                .orElseThrow(() -> new ProblemReportNotFoundException("Problem report not found with id: " + problemReportId));

    }

    @Override
    public PageResponse<ProblemReportResponse> getProblemReportsByUserId(UUID userId, int pageNo, int pageSize) throws UserNotFoundException {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProblemReport> problemReports = problemReportRepository.findAllByUserUuidID(userId, pageable);

        return getProblemReportResponsePageResponse(problemReports);


    }

    @Override
    public PageResponse<ProblemReportResponse> getProblemReportsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize)
            throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found with signature: " + apartmentSignature));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProblemReport> problemReports = problemReportRepository.findAllByApartmentUuidID(apartment.getUuidID(), pageable);

        return getProblemReportResponsePageResponse(problemReports);

    }

    @Override
    public PageResponse<ProblemReportResponse> getAllProblemReports(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProblemReport> problemReports = problemReportRepository.findAll(pageable);

        return getProblemReportResponsePageResponse(problemReports);

    }

    @Override
    public PageResponse<ProblemReportResponse> getProblemReportsByStatus(ReportStatus status, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProblemReport> problemReports = problemReportRepository.findAllByReportStatus(status, pageable);

        return getProblemReportResponsePageResponse(problemReports);

    }

    private PageResponse<ProblemReportResponse> getProblemReportResponsePageResponse(Page<ProblemReport> problemReports) {
        return new PageResponse<>(
                problemReports.getNumber(),
                problemReports.getSize(),
                problemReports.getTotalPages(),
                problemReports.getContent().stream()
                        .map(problemReport -> ProblemReportResponse.builder()
                                .id(problemReport.getId())
                                .note(problemReport.getNote())
                                .reportStatus(problemReport.getReportStatus())
                                .category(problemReport.getCategory())
                                .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                                .apartmentAddress(problemReport.getApartment().getAddress())
                                .endDate(problemReport.getEndDate())
                                .build())
                        .toList(),
                problemReports.isLast(),
                problemReports.hasNext(),
                problemReports.hasPrevious()
        );
    }

}
