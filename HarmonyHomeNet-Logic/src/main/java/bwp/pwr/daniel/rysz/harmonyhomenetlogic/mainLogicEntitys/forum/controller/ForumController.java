package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.PostNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.TopicNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.ForumService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.PostService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.TopicService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.enums.TopicCategory;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.ForumRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.PostRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.requests.forumStaff.TopicRequest;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.ForumResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.PostResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.TopicResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bwp/api/v1/forum")
public class ForumController {

    private final ForumService forumService;
    private final PostService postService;
    private final TopicService topicService;

    @GetMapping("/all")
    public ResponseEntity<List<ForumResponse>> getAllForums() {
        List<ForumResponse> forums = forumService.mapForumListToForumResponseList(forumService.findAll());
        return forums.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(forums);
    }

    @GetMapping("/forum-by-id/{forumId}")
    public ResponseEntity<ForumResponse> getForumById(@PathVariable String forumId) {
        return ResponseEntity.ok(forumService.mapForumToForumResponse(forumService.findById(UUID.fromString(forumId))));
    }

    @GetMapping("/forum-by-name/{forumName}")
    public ResponseEntity<ForumResponse> getForumByName(@PathVariable String forumName) {
        return ResponseEntity.ok(forumService.mapForumToForumResponse(forumService.findByForumName(forumName)));
    }

    @GetMapping("/topics-by-category/{topicCategory}")
    public ResponseEntity<List<TopicResponse>> getTopicsByCategory(@PathVariable String topicCategory) {
        return ResponseEntity.ok(topicService.mapTopicListToTopicResponseList(topicService.findByTopicCategory(TopicCategory.valueOf(topicCategory))));
    }

    @GetMapping("/posts-by-user/{userLogin}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable String userLogin) {
        return ResponseEntity.ok(postService.mapPostListToPostResponseList(postService.findPostByUserLogin(userLogin)));
    }

    @PostMapping("/add-new-forum")
    public ResponseEntity<ForumResponse> addForum(@RequestBody ForumRequest newForum) {
        return ResponseEntity.created(null)
                .body(forumService.save(getForum(newForum)));
    }


    @PutMapping("/add-topic-to-forum/{forumId}")
    public ResponseEntity<TopicResponse> addTopicToForum(@PathVariable String forumId, @RequestBody TopicRequest newTopic) {
        UUID forumUUID = UUID.fromString(forumId);
        return ResponseEntity.created(null).body(
                topicService.addTopicToForum(
                        forumUUID,
                        getTopic(newTopic)
                )
        );
    }

    @PutMapping("/remove-topic/{topicId}/forum-id/{forumId}")
    public ResponseEntity<String> removeTopicFromForum(@PathVariable String topicId, @PathVariable String forumId) {
        topicService.deleteTopicFromForum(
                UUID.fromString(forumId),
                UUID.fromString(topicId)
        );
        return ResponseEntity.ok("Topic " + topicId + " removed from forum " + forumId);
    }

    @PutMapping("/add-post/topic/{topicId}/user-login/{userLogin}")
    public ResponseEntity<PostResponse> addPostToTopic(@PathVariable String topicId, @PathVariable String userLogin, @RequestBody PostRequest postRequest){
        return ResponseEntity.created(null).body(
                postService.addPostToTopic(
                        UUID.fromString(topicId),
                        userLogin,
                        getPost(postRequest)
                )
        );
    }

    @PutMapping("/remove-post/{postId}/topic/{topicId}")
    public ResponseEntity<String> removePostFromTopic(@PathVariable String postId, @PathVariable String topicId) throws TopicNotFoundException, PostNotFoundException {
        postService.deletePostFromTopic(
                UUID.fromString(postId),
                UUID.fromString(topicId)
        );
        return ResponseEntity.ok("Post " + postId + " removed from topic " + topicId);
    }

    @DeleteMapping("/delete-forum/{forumId}")
    public ResponseEntity<String> deleteForum(@PathVariable String forumId) {
        forumService.deleteById(UUID.fromString(forumId));
        return ResponseEntity.ok("Forum " + forumId + " deleted");
    }


    private Forum getForum(@NonNull ForumRequest newForum) {
        return Forum.builder()
                .forumName(newForum.forumName())
                .forumDescription(newForum.forumDescription())
                .build();
    }

    private Topic getTopic(@NonNull TopicRequest newTopic) {
        return Topic.builder()
                .topicName(newTopic.topicName())
                .topicCategory(TopicCategory.valueOf(newTopic.topicCategory()))
                .build();
    }

    private Post getPost(@NonNull PostRequest postRequest) {
        return Post.builder()
                .postContent(postRequest.postContent())
                .build();
    }
}
