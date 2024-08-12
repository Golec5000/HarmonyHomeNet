package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "post_content", nullable = false, length = 1000)
    private String postContent;

    @Column(name = "post_add_date", nullable = false)
    private LocalDateTime postAddDate;

    @JoinColumn(name = "topic_id")
    @ManyToOne
    @JsonBackReference
    private Topic topic;

    @JoinColumn(name = "resident_id")
    @ManyToOne
    @JsonBackReference
    private Resident resident;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        postAddDate = LocalDateTime.now();
    }
}
