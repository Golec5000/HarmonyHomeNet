package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.DocumentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentRequest {

    @NotEmpty
    @Size(max = 50)
    private String documentName;

    @NotEmpty
    @Size(max = 15)
    private DocumentType documentType;

    @NotNull
    private byte[] documentData;

}
