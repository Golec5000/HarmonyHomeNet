package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDeleteRequest {

    @Nonnull
    private UUID documentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID userId;

    private boolean deleteCompletely;

}
