package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.ForumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumServiceImp implements ForumService {

    private final ForumRepository forumRepository;

    @Override
    public List<Forum> findAll() {
        return forumRepository.findAll();
    }

    @Override
    public void save(Forum forum) {
        forumRepository.save(forum);
    }

    @Override
    public Optional<Forum> findById(UUID id) {
        return forumRepository.findById(id);
    }

    @Override
    public Optional<Forum> findByForumName(String forumName) {
        return forumRepository.findByForumName(forumName);
    }

    @Override
    public void deleteById(UUID id) {
        forumRepository.deleteById(id);
    }
}
