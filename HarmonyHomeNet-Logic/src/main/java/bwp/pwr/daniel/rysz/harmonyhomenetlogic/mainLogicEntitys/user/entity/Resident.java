package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.ApartmentResidentAssignment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.BasementResidentAssignment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "residents")
public class Resident extends User {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @JsonManagedReference
    private List<Basement> basementList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @JsonManagedReference
    private List<ParkingSpace> parkingSpaces;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private List<ApartmentResidentAssignment> apartmentResidentAssignments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private List<BasementResidentAssignment> basementResidentAssignments;


}
