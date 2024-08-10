package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "residents")
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "PESEL_number", nullable = false, unique = true)
    private String PESELNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "resident_roles", joinColumns = @JoinColumn(name = "resident_id"))
    private Set<Role> role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @JsonManagedReference
    private List<Basement> basementList;

    @JoinColumn(name = "apartment_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Apartment apartment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    @JsonManagedReference
    private List<ParkingSpace> parkingSpaces;

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts;

    @PrePersist
    @PreUpdate
    public void checkPESELNumber(){
        if (PESELNumber == null || PESELNumber.isEmpty() || PESELNumber.isBlank())
            throw new IllegalArgumentException("PESEL number cannot be null or empty");

        if (!PESELNumber.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("PESEL number must contain only digits");

        if (PESELNumber.length() != 11)
            throw new IllegalArgumentException("PESEL number must contain 11 digits");

    }

}
