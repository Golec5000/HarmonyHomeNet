package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ForumNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.PostNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.TopicNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.ForumService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.PostService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.TopicService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service.UserService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.ForumRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.PostRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.TopicRequest;
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
    private final UserService userService;

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

    @GetMapping("/topics-by-category/{topicCategory}")
    public ResponseEntity<List<Topic>> getTopicsByCategory(@PathVariable String topicCategory) {
        return ResponseEntity.ok(topicService.findByTopicCategory(TopicCategory.valueOf(topicCategory)));
    }

    @GetMapping("/posts-by-user/{userLogin}")
    public ResponseEntity<List<Post>> getPostsByResident(@PathVariable String userLogin) {
        return ResponseEntity.ok(postService.findPostByUserLogin(userLogin));
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
                .forum(forum)
                .build();

        forum.getTopics().add(topic);
        topicService.save(topic);
        forumService.save(forum);

        return ResponseEntity.ok(topic);
    }

    @PutMapping("/remove-topic/{topicId}/forum-id/{forumId}")
    public ResponseEntity<Forum> removeTopicFromForum(@PathVariable String topicId, @PathVariable String forumId) throws ForumNotFoundException, TopicNotFoundException {
        UUID topicUUID = UUID.fromString(topicId);

        Forum forum = forumService.findById(UUID.fromString(forumId))
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));

        Topic topic = topicService.findById(topicUUID)
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));

        forum.getTopics().remove(topic);
        topicService.deleteById(topicUUID);
        forumService.save(forum);

        return ResponseEntity.ok(forum);
    }

    @PutMapping("/add-post/topic/{topicId}/user-login/{userLogin}")
    public ResponseEntity<Post> addPostToTopic(@PathVariable String topicId, @PathVariable String userLogin, @RequestBody PostRequest postRequest) throws TopicNotFoundException, UserNotFoundException {

        Topic topic = topicService.findById(UUID.fromString(topicId))
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));

        User user = userService.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));

        if (topic.getPosts() == null) topic.setPosts(new ArrayList<>());
        if (user.getPosts() == null) user.setPosts(new ArrayList<>());

        Post post = Post.builder()
                .postContent(postRequest.getPostContent())
                .user(user)
                .topic(topic)
                .build();

        topic.getPosts().add(post);
        user.getPosts().add(post);

        postService.save(post);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/remove-post/{postId}/topic/{topicId}")
    public ResponseEntity<Topic> removePostFromTopic(@PathVariable String postId, @PathVariable String topicId) throws TopicNotFoundException, PostNotFoundException {
        UUID postUUID = UUID.fromString(postId);
        UUID topicUUID = UUID.fromString(topicId);

        Topic topic = topicService.findById(topicUUID)
                .orElseThrow(() -> new TopicNotFoundException("wrong topic id"));

        Post post = postService.findById(postUUID)
                .orElseThrow(() -> new PostNotFoundException("wrong post id"));

        User user = post.getUser();

        user.getPosts().remove(post);
        topic.getPosts().remove(post);

        postService.deleteById(postUUID);
        topicService.save(topic);
        userService.save(user);

        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/delete-forum/{forumId}")
    public ResponseEntity<String> deleteForum(@PathVariable String forumId) throws ForumNotFoundException {
        UUID id = UUID.fromString(forumId);
        Forum forum = forumService.findById(id)
                .orElseThrow(() -> new ForumNotFoundException("wrong forum id"));
        forumService.deleteById(id);
        return ResponseEntity.ok("Forum " + forum.getForumName() + " deleted");
    }

}
