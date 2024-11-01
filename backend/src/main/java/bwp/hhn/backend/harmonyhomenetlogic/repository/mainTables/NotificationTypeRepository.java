package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    void deleteByTypeAndUserUuidID (@NotNull Notification type, UUID user_uuidID);

    @Query("SELECT nt FROM NotificationType nt JOIN nt.user u WHERE u.email = :email")
    List<NotificationType> findByUserEmail(@Param("email") String email);

}