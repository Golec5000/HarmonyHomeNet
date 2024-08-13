package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity;


import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
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
        if (PESELNumber == null || PESELNumber.isBlank())
            throw new IllegalArgumentException("PESEL number cannot be null or empty");

        if (!PESELNumber.matches("\\d{11}"))
            throw new IllegalArgumentException("PESEL number must contain 11 digits");

        if (!isChecksumValid(PESELNumber))
            throw new IllegalArgumentException("Invalid PESEL checksum");

        if (!isDateValid(PESELNumber))
            throw new IllegalArgumentException("Invalid PESEL date");

        if (isFutureDate(PESELNumber))
            throw new IllegalArgumentException("PESEL date cannot be in the future");
    }

    private boolean isChecksumValid(String pesel) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(pesel.charAt(i)) * weights[i];
        }
        int checksum = (10 - (sum % 10)) % 10;
        return checksum == Character.getNumericValue(pesel.charAt(10));
    }

    private boolean isDateValid(String pesel) {
        List<Integer> tmp = dates(pesel);

        try {
            LocalDate date = LocalDate.of(tmp.get(0), tmp.get(1), tmp.get(2));
            return date.getDayOfMonth() == tmp.get(2);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFutureDate(String pesel) {
        List<Integer> tmp = dates(pesel);
        LocalDate date = LocalDate.of(tmp.get(0), tmp.get(1), tmp.get(2));
        return date.isAfter(LocalDate.now());
    }

    private List<Integer> dates(String pesel) {
        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));
        return List.of(year, month, day);
    }
}
