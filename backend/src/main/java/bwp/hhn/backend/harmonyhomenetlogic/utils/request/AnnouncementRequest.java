package bwp.hhn.backend.harmonyhomenetlogic.utils.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnouncementRequest {

    @NotEmpty
    @Size(max = 50)
    private String title;

    @NotEmpty
    @Size(max = 1000)
    private String content;

    @NotEmpty
    private UUID userId;
}
