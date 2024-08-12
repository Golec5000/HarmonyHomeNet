package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicService {
    List<Topic> findAll();
    void save(Topic topic);
    Optional<Topic> findById(UUID id);
    Optional<Topic> findByTopicName(String topicName);
    List<Topic> findByTopicCategory(TopicCategory topicCategory);
    void deleteById(UUID id);
}
