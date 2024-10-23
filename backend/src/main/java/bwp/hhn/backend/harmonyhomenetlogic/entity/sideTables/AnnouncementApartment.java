package bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Announcement_apartments", indexes = {
        @Index(name = "idx_announcement_apartment", columnList = "apartment_id"),
        @Index(name = "idx_announcement", columnList = "announcement_id")
})
public class AnnouncementApartment {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "UUID_id")
    @JsonBackReference
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "announcement_id", referencedColumnName = "ID")
    @JsonBackReference
    private Announcement announcement;
}
