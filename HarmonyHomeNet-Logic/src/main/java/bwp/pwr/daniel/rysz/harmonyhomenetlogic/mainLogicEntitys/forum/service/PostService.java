package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    List<Post> findAll();
    void save(Post post);
    void deleteById(UUID id);
    Optional<Post> findById(UUID id);
    List<Post> findPostByUserLogin(String login);
}
