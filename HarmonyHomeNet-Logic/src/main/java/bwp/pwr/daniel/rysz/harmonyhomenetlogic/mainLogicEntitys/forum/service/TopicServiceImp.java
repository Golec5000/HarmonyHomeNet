package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.TopicRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImp implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Override
    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public Optional<Topic> findById(UUID id) {
        return topicRepository.findById(id);
    }

    @Override
    public Optional<Topic> findByTopicName(String topicName) {
        return topicRepository.findByTopicName(topicName);
    }

    @Override
    public List<Topic> findByTopicCategory(TopicCategory topicCategory) {
        return topicRepository.findByTopicCategory(topicCategory);
    }

    @Override
    public void deleteById(UUID id) {
        topicRepository.deleteById(id);
    }
}
