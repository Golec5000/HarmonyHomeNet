package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.ProblemReport;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProblemReportRepository extends JpaRepository<ProblemReport, Long> {

    List<ProblemReport> findAllByUserUuidID(UUID userId);

    List<ProblemReport> findAllByApartmentUuidID(UUID apartmentId);

    List<ProblemReport> findAllByReportStatus(ReportStatus status);

}