package bwp.pwr.daniel.rysz.harmonyhomenetlogic.building.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "basements")
public class Basement {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "building_number")
    private int basementNumber;

    @Column(name = "area")
    private int area;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

}