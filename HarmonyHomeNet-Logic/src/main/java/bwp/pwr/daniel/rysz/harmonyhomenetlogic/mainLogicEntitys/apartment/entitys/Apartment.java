package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entitys.Resident;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "apartments")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "apartment_number")
    private int apartmentNumber;

    @Column(name = "area")
    private BigDecimal area;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apartment")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Resident> residents;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

}