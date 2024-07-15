package bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "building")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Basement> basements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "building")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ParkingSpace> parkingSpaces;

}