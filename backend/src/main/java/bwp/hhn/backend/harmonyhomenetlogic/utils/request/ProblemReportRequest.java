package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Category;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.ReportStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemReportRequest {

    @NotEmpty
    @Size(max = 1000)
    String note;

    @NotNull
    private ReportStatus reportStatus;

    @NotNull
    private Category category;

    @NotEmpty
    private UUID userId;

    @NotEmpty
    private UUID apartmentId;

}
