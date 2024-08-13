package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "basements")
public class Basement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "basement_number")
    private int basementNumber;

    @Column(name = "area")
    private BigDecimal area;

    @JoinColumn(name = "resident_id")
    @ManyToOne
    @JsonBackReference
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;
}