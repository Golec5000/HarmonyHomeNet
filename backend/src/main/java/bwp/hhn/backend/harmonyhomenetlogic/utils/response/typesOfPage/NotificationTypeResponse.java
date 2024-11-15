package bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage;

import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import lombok.Builder;

@Builder
public record NotificationTypeResponse(
        Notification type
) {
}
