package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.TopicNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.TopicResponse;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicService {
    List<Topic> findAll();

    TopicResponse save(@NonNull Topic topic);

    Topic findById(UUID id) throws TopicNotFoundException;

    List<Topic> findByTopicCategory(TopicCategory topicCategory);

    void deleteById(UUID id) throws TopicNotFoundException;

    TopicResponse addTopicToForum(UUID forumId,@NonNull Topic topic) throws ForumNotFoundException;

    void deleteTopicFromForum(UUID forumId, UUID topicId) throws ForumNotFoundException, TopicNotFoundException;

    TopicResponse mapTopicToTopicResponse(@NonNull Topic topic);

    List<TopicResponse> mapTopicListToTopicResponseList(@NonNull List<Topic> topics);
}
