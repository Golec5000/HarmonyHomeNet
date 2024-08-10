package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.TopicNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.ForumService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.PostService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.TopicService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.service.ResidentService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.ForumRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.PostRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.TopicRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bwp/api/v1/forum")
public class ForumController {

    private final TopicService topicService;
    private final ForumService forumService;
    private final PostService postService;
    private final ResidentService residentService;

    @GetMapping("/all")
    public ResponseEntity<List<Forum>> getAllForums() {
        return ResponseEntity.ok(forumService.findAll());
    }

    @GetMapping("/forum-by-id/{forumId}")
    public ResponseEntity<Forum> getForumById(@PathVariable String forumId) throws ForumNotFoundException {
        UUID id = UUID.fromString(forumId);
        return forumService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));
    }

    @GetMapping("/forum-by-name/{forumName}")
    public ResponseEntity<Forum> getForumByName(@PathVariable String forumName) throws ForumNotFoundException {
        return forumService.findByForumName(forumName)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum name"));
    }

    @PostMapping("/add-new-forum")
    public ResponseEntity<Forum> addForum(@RequestBody ForumRequest newForum) {
        Forum forum = Forum.builder()
                .forumName(newForum.getForumName())
                .forumDescription(newForum.getForumDescription())
                .build();
        forumService.save(forum);
        return ResponseEntity.ok(forum);
    }

    @PutMapping("/add-topic-to-forum/{forumId}")
    public ResponseEntity<Topic> addTopicToForum(@PathVariable String forumId, @RequestBody TopicRequest newTopic) throws ForumNotFoundException {
        UUID id = UUID.fromString(forumId);
        Forum forum = forumService.findById(id)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));

        if (forum.getTopics() == null) forum.setTopics(new ArrayList<>());

        Topic topic = Topic.builder()
                .topicName(newTopic.getTopicName())
                .topicCategory(TopicCategory.valueOf(newTopic.getTopicCategory()))
                .build();

        forum.getTopics().add(topic);
        topicService.save(topic);
        forumService.save(forum);

        return ResponseEntity.ok(topic);
    }

    @PutMapping("remove-topic/{topicId}/forum-id/{forumId}")
    public ResponseEntity<Forum> removeTopicFromForum(@PathVariable String topicId, @PathVariable String forumId) throws ForumNotFoundException, TopicNotFoundException {
        UUID forumUUID = UUID.fromString(forumId);
        UUID topicUUID = UUID.fromString(topicId);

        Forum forum = forumService.findById(forumUUID)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));

        Topic topic = topicService.findById(topicUUID)
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));

        forum.getTopics().remove(topic);
        topicService.deleteById(topicUUID);
        forumService.save(forum);

        return ResponseEntity.ok(forum);
    }

    @PutMapping("/add-post/topic/{topicId}/user-id/{userId}")
    public ResponseEntity<Post> addPostToTopic(@PathVariable String topicId, @PathVariable String userId, @RequestBody PostRequest newPost) throws TopicNotFoundException, UserNotFoundException {
        UUID toppicUUID = UUID.fromString(topicId);
        UUID residentUUID = UUID.fromString(userId);

        Topic topic = topicService.findById(toppicUUID)
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));

        Resident resident = residentService.findById(residentUUID)
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));

        if (topic.getPosts() == null) topic.setPosts(new ArrayList<>());
        if (resident.getPosts() == null) resident.setPosts(new ArrayList<>());

        Post post = Post.builder()
                .postContent(newPost.getPostContent())
                .build();

        topic.getPosts().add(post);
        resident.getPosts().add(post);

        residentService.save(resident);
        postService.save(post);
        topicService.save(topic);

        return ResponseEntity.ok(post);
    }


}
