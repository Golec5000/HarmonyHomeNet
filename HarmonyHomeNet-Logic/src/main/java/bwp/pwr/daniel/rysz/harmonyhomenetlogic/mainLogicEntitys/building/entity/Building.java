package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "building_name", unique = true)
    private String buildingName;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "region", nullable = false)
    private String region;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "building")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Basement> basements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "building")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ParkingSpace> parkingSpaces;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "building")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Apartment> apartments;

    @JoinColumn(name = "employee_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

}