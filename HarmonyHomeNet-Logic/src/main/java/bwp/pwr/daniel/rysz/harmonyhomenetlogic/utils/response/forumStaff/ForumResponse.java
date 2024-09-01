package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ForumResponse(
        UUID id,
        String forumName,
        String forumDescription
) {
}
