package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity;


import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Gender;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.DateTimeException;
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

    @Column(name = "login", nullable = false, unique = true, updatable = false)
    private String login;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "PESEL_number", unique = true)
    private String PESELNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_gender")
    @Enumerated(EnumType.STRING)
    private Gender userGender;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts;

    @PrePersist
    public void prePersist() {
        creatUserLogin();
        checkPESELNumber();
    }

    @PreUpdate
    public void preUpdate() {
        checkPESELNumber();
    }

    private void creatUserLogin() {
        login = String.format("%s.%s.%s",
                firstName.substring(0, 3),
                lastName.substring(0, 3),
                id.toString().substring(0, 6));
    }

    private void checkPESELNumber() {
        if (PESELNumber != null && !PESELNumber.isBlank() && userGender != null) {
            validatePESELNumber();
        }
    }

    private void validatePESELNumber() {
        if (!PESELNumber.matches("\\d{11}"))
            throw new IllegalArgumentException("PESEL number must contain 11 digits");

        if (!isChecksumValid(PESELNumber))
            throw new IllegalArgumentException("Invalid PESEL checksum");

        if (!isGenderValid(PESELNumber, userGender))
            throw new IllegalArgumentException("Invalid PESEL that does not match owner gender");

        if (!isDateValid(PESELNumber))
            throw new IllegalArgumentException("Invalid PESEL date (or date in the future)");
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
        try {
            List<Integer> tmp = dates(pesel);
            LocalDate date = LocalDate.of(tmp.get(0), tmp.get(1), tmp.get(2));
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeException e) {
            return false;
        }
    }

    private boolean isGenderValid(String pesel, Gender gender) {
        boolean isMale = Character.getNumericValue(pesel.charAt(9)) % 2 == 1;
        return (gender == Gender.MALE && isMale) || (gender == Gender.FEMALE && !isMale);
    }

    private List<Integer> dates(String pesel) {
        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));

        if (month > 80) {
            year += 1800;
            month -= 80;
        } else if (month > 60) {
            year += 2200;
            month -= 60;
        } else if (month > 40) {
            year += 2100;
            month -= 40;
        } else if (month > 20) {
            year += 2000;
            month -= 20;
        } else {
            year += 1900;
        }

        return List.of(year, month, day);
    }

}
