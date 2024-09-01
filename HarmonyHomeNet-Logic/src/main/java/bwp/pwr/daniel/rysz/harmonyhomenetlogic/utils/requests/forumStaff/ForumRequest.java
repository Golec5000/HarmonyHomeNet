package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public record ForumRequest(
        String forumName,
        String forumDescription
) {}