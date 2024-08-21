package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.voting.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.VoteAns;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vote_options")
public class VoteCast {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JoinColumn(name = "vote_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Vote vote;

    @Column(name = "option_name", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private VoteAns optionName;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Resident resident;

    @Column(name = "time_stamp", nullable = false, updatable = false)
    private LocalDateTime timeStamp;
}
