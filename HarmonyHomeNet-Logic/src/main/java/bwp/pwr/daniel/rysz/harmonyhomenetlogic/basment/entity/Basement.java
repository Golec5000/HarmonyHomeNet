package bwp.pwr.daniel.rysz.harmonyhomenetlogic.basment.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entity.Building;
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

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

}