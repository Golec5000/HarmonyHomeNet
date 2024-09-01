package bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;

import java.util.UUID;

public record TopicResponse(
        UUID id,
        String topicName,
        TopicCategory topicCategory
) {
}
