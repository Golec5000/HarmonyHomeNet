package bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "Refresh_Token", indexes = {
        @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Token", nullable = false, length = 10000)
    private String refreshToken;

    @Column(name = "Revoked")
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "UUID_id")
    @JsonBackReference
    private User user;

}
