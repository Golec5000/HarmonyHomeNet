package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ForumRequest {

    private String forumName;

    private String forumDescription;

}
