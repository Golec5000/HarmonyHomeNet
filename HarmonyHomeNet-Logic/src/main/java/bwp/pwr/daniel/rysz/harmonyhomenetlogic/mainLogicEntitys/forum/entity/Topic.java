package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "topic_name", nullable = false, unique = true)
    private String topicName;

    @Column(name = "topic_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private TopicCategory topicCategory;

    @Column(name = "topic_add_date", nullable = false)
    private LocalDateTime topicAddDate;

    @ManyToOne
    @JoinColumn(name = "forum_id", nullable = false)
    @JsonBackReference
    private Forum forum;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Post> posts;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        topicAddDate = LocalDateTime.now();
    }

}
