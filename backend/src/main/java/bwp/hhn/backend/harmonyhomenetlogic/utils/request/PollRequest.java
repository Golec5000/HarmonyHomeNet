package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollRequest {

    @NotEmpty
    @Size(max = 100)
    private String pollName;

    @NotEmpty
    @Size(max = 1000)
    private String content;

    @NotNull
    private byte[] uploadData;

    @NotNull
    private LocalDateTime endDate;

}