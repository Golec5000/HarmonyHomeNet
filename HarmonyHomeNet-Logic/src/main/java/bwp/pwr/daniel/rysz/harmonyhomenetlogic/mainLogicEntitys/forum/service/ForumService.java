package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForumService {
    List<Forum> findAll();
    void save(Forum forum);
    Optional<Forum> findById(UUID id);
    Optional<Forum> findByForumName(String forumName);
    void deleteById(UUID id);
}
