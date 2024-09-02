package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TopicResponse(
        UUID id,
        String topicName,
        TopicCategory topicCategory
) {
}
