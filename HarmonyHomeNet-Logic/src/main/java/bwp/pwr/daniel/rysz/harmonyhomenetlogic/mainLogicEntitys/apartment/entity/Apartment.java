package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.building.entity.Building;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;

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

    @Column(name = "apartment_number", nullable = false)
    private int apartmentNumber;

    @Column(name = "area", nullable = false)
    private BigDecimal area;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ApartmentResidentAssignment> apartmentAssignments;

    @PrePersist
    public void prePersist() {
        checkApartmentNumber();
        checkArea();
    }

    @PreUpdate
    public void preUpdate() {
        checkApartmentNumber();
        checkArea();
    }


    private void checkApartmentNumber() {
        if (apartmentNumber <= 0) {
            throw new InvalidDataAccessApiUsageException("Apartment number cannot be negative or zero");
        }

        if(building == null) {
            return;
        }

        boolean isNotApartmentNumberUnique = building.getApartments() != null && building.getApartments().stream()
                .anyMatch(apartment -> !apartment.equals(this) && apartment.getApartmentNumber() == apartmentNumber);

        if (isNotApartmentNumberUnique) {
            throw new InvalidDataAccessApiUsageException("Apartment number must be unique in selected building");
        }
    }

    private void checkArea() {
        if (area.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataAccessApiUsageException("Area cannot be negative or zero");
        }
    }

}