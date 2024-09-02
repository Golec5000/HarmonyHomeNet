package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.ForumResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ForumService {
    List<Forum> findAll();

    ForumResponse save(@NonNull Forum forum);

    Forum findById(UUID id) throws ForumNotFoundException;

    Forum findByForumName(String forumName) throws ForumNotFoundException;

    void deleteById(UUID id) throws ForumNotFoundException;

    ForumResponse mapForumToForumResponse(@NonNull Forum forum);

    List<ForumResponse> mapForumListToForumResponseList(@NonNull List<Forum> forums);

}
