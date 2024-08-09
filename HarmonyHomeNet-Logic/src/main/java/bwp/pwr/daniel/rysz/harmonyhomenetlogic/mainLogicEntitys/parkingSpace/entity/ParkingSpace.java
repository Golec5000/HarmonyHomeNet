package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
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
@Table(name = "parking_spaces")
public class ParkingSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "parking_space_number")
    private int number;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    @JsonBackReference
    private Resident resident;
}