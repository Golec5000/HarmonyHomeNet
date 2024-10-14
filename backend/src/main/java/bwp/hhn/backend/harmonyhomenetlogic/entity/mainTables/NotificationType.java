package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.utils.Notification;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Notification_types", indexes = {
        @Index(name = "idx_notification_users_id", columnList = "users_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_notificationtype_type", columnNames = {"users_id", "Type"})
})
public class NotificationType {

    @Id
    @Column(name = "ID", unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false, length = 5)
    private Notification type;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;
}
