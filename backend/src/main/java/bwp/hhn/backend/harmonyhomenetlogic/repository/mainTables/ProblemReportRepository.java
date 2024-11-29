package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.ProblemReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProblemReportRepository extends JpaRepository<ProblemReport, Long> {

    Page<ProblemReport> findAllByApartmentUuidID(UUID apartmentId, Pageable pageable);

}