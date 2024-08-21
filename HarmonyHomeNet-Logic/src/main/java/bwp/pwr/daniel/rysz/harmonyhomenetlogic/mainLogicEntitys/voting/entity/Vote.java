package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.voting.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.VoteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "vote_title", nullable = false, unique = true, length = 100)
    private String voteTitle;

    @Column(name = "vote_description", nullable = false, length = 500)
    private String voteDescription;

    @Column(name = "vote_start_date", nullable = false)
    private LocalDateTime voteStartDate;

    @Column(name = "vote_end_date", nullable = false)
    private LocalDateTime voteEndDate;

    @Column(name = "vote_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus;

    @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY)
    private List<VoteCast> voteCasts;

    @OneToOne(mappedBy = "vote", cascade = CascadeType.ALL)
    private VoteResult voteResult;

    @PrePersist
    public void prePersist() {
        checkDates();
    }

    @PreUpdate
    public void preUpdate() {
        checkDates();
        createVoteResult();
    }

    private void checkDates() {
        if (voteStartDate.isAfter(voteEndDate)) {
            throw new IllegalArgumentException("Vote start date cannot be after vote end date");
        }

        if (voteStartDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Vote start date cannot be in the past");
        }

        if (voteStartDate.isEqual(voteEndDate)) {
            throw new IllegalArgumentException("Vote start date cannot be equal to vote end date");
        }
    }

    private void createVoteResult(){

        if(voteStatus != VoteStatus.CLOSED){
            return;
        }



    }

}
