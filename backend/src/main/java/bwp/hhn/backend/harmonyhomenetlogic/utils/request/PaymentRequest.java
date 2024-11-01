package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    @NotEmpty
    private String apartmentSignature;

    @NotEmpty
    private String description;

}
