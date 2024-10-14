package bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Announcements", indexes = {
        @Index(name = "idx_announcement_user_id", columnList = "user_id"),
        @Index(name = "idx_announcement_created_at", columnList = "created_at"),
        @Index(name = "idx_announcement_updated_at", columnList = "updated_at")
})
public class Announcement {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AnnouncementApartment> announcementApartments;
}