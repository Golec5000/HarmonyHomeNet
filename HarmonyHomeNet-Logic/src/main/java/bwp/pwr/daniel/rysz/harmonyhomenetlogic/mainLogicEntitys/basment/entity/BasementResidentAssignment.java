package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.ResourceRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "basement_resident_assignments")
public class BasementResidentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    @JsonBackReference
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonBackReference
    private Basement basement;

    @Column(name = "role")
    private ResourceRole resourceRole = ResourceRole.BASEMENT_OWNER;

}
