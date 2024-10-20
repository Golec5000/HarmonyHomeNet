package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    void deleteByTypeAndUserUuidID (@NotNull Notification type, UUID user_uuidID);

}