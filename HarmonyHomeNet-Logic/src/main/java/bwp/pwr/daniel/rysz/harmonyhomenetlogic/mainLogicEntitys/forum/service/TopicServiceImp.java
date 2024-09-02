package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.TopicNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.TopicRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.TopicResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImp implements TopicService {

    private final TopicRepository topicRepository;
    private final ForumService forumService;

    @Override
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Override
    public TopicResponse save(@NonNull Topic topic) {
        topicRepository.save(topic);
        return mapTopicToTopicResponse(topic);
    }

    @Override
    public Topic findById(UUID id) throws TopicNotFoundException {
        return topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));
    }

    @Override
    public List<Topic> findByTopicCategory(TopicCategory topicCategory) {
        return topicRepository.findByTopicCategory(topicCategory);
    }

    @Override
    public void deleteById(UUID id) throws TopicNotFoundException {
        if (topicRepository.existsById(id)) topicRepository.deleteById(id);
        else throw new TopicNotFoundException("wrong topic id");
    }

    @Override
    @Transactional
    public TopicResponse addTopicToForum(UUID forumId, @NonNull Topic topic) throws ForumNotFoundException {
        Forum forum = forumService.findById(forumId);

        if(forum.getTopics() == null) forum.setTopics(new ArrayList<>());
        forum.getTopics().add(topic);

        topic.setForum(forum);
        topicRepository.save(topic);
        forumService.save(forum);

        return mapTopicToTopicResponse(topic);
    }

    @Override
    @Transactional
    public void deleteTopicFromForum(UUID forumId, UUID topicId) throws ForumNotFoundException, TopicNotFoundException {
        Forum forum = forumService.findById(forumId);
        Topic topic = findById(topicId);

        forum.getTopics().remove(topic);
        topicRepository.deleteById(topicId);
        forumService.save(forum);

    }

    @Override
    public TopicResponse mapTopicToTopicResponse(@NonNull Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .topicCategory(topic.getTopicCategory())
                .build();
    }

    @Override
    public List<TopicResponse> mapTopicListToTopicResponseList(@NonNull List<Topic> topics) {
        return topics.stream()
                .map(this::mapTopicToTopicResponse)
                .toList();
    }
}
