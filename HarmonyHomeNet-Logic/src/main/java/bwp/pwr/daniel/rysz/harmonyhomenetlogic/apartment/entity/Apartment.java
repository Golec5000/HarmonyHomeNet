package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartment.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.entity.Building;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "apartment_number")
    private int apartmentNumber;

    @Column(name = "area")
    private BigDecimal area;

    @Column(name = "owners")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apartment")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Owner> owners;

    @Column(name = "tenants")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apartment")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Tenant> tenants;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;


}
