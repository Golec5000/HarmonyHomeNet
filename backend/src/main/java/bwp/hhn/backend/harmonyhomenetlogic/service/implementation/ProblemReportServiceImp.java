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
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.ProblemReportService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.ProblemReportRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.ProblemReportResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemReportServiceImp implements ProblemReportService {

    private final ProblemReportRepository problemReportRepository;
    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;


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

        if (ReportStatus.DONE.equals(problemReportRequest.getReportStatus())) problemReportToUpdate.setEndDate(LocalDateTime.now());

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
    public List<ProblemReportResponse> getProblemReportsByUserId(UUID userId) throws UserNotFoundException {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return problemReportRepository.findAllByUserUuidID(userId)
                .stream()
                .map(problemReport -> ProblemReportResponse.builder()
                        .id(problemReport.getId())
                        .note(problemReport.getNote())
                        .reportStatus(problemReport.getReportStatus())
                        .category(problemReport.getCategory())
                        .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                        .apartmentAddress(problemReport.getApartment().getAddress())
                        .endDate(problemReport.getEndDate())
                        .build())
                .toList();


    }

    @Override
    public List<ProblemReportResponse> getProblemReportsByApartmentSignature(String apartmentSignature) throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found with signature: " + apartmentSignature));

        return problemReportRepository.findAllByApartmentUuidID(apartment.getUuidID())
                .stream()
                .map(problemReport -> ProblemReportResponse.builder()
                        .id(problemReport.getId())
                        .note(problemReport.getNote())
                        .reportStatus(problemReport.getReportStatus())
                        .category(problemReport.getCategory())
                        .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                        .apartmentAddress(problemReport.getApartment().getAddress())
                        .endDate(problemReport.getEndDate())
                        .build())
                .toList();

    }

    @Override
    public List<ProblemReportResponse> getAllProblemReports() {

        return problemReportRepository.findAll()
                .stream()
                .map(problemReport -> ProblemReportResponse.builder()
                        .id(problemReport.getId())
                        .note(problemReport.getNote())
                        .reportStatus(problemReport.getReportStatus())
                        .category(problemReport.getCategory())
                        .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                        .apartmentAddress(problemReport.getApartment().getAddress())
                        .endDate(problemReport.getEndDate())
                        .build())
                .toList();

    }

    @Override
    public List<ProblemReportResponse> getProblemReportsByStatus(ReportStatus status) {

        return problemReportRepository.findAllByReportStatus(status)
                .stream()
                .map(problemReport -> ProblemReportResponse.builder()
                        .id(problemReport.getId())
                        .note(problemReport.getNote())
                        .reportStatus(problemReport.getReportStatus())
                        .category(problemReport.getCategory())
                        .userName(problemReport.getUser().getFirstName() + " " + problemReport.getUser().getLastName())
                        .apartmentAddress(problemReport.getApartment().getAddress())
                        .endDate(problemReport.getEndDate())
                        .build())
                .toList();

    }
}
