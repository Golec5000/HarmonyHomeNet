package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRequest {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
