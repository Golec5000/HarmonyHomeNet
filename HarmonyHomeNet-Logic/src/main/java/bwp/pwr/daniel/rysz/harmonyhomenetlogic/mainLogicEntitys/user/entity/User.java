package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity;


import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

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
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> role;

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
