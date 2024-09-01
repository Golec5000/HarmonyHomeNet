package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostResponse(
        UUID id,
        String postContent,
        String postAuthor
) {
}
