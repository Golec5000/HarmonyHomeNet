package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID> {
    Optional<Forum> findByForumName(String forumName);
}
