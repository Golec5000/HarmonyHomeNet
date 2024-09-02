package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.ForumRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.ForumResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public ForumResponse save(@NonNull Forum forum) {
        forumRepository.save(forum);
        return mapForumToForumResponse(forum);
    }

    @Override
    public Forum findById(UUID id) throws ForumNotFoundException {
        return forumRepository.findById(id)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));
    }

    @Override
    public Forum findByForumName(String forumName) throws ForumNotFoundException {
        return forumRepository.findByForumName(forumName)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum name"));
    }

    @Override
    public void deleteById(UUID id) throws ForumNotFoundException {
        if (forumRepository.existsById(id)) forumRepository.deleteById(id);
        else throw new ForumNotFoundException("wrong forum id");
    }

    @Override
    public ForumResponse mapForumToForumResponse(@NonNull Forum forum) {
        return ForumResponse.builder()
                .id(forum.getId())
                .forumName(forum.getForumName())
                .forumDescription(forum.getForumDescription())
                .build();
    }

    @Override
    public List<ForumResponse> mapForumListToForumResponseList(@NonNull List<Forum> forums) {
        return forums.stream()
                .map(this::mapForumToForumResponse)
                .toList();
    }
}
