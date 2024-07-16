package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Apartment;
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

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "region")
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

}