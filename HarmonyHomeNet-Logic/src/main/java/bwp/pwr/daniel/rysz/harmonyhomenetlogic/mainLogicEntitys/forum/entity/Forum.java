package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "forums")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "forum_name", nullable = false, unique = true)
    private String forumName;

    @Column(name = "forum_description")
    private String forumDescription;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Topic> topics;

}
