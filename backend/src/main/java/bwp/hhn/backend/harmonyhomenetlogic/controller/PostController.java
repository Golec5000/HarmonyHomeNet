package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PostService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/forum")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // GET
    @GetMapping("/get-all-topics")
    public ResponseEntity<PageResponse<TopicResponse>> getAllTopics(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(postService.getAllTopics(pageNo, pageSize));
    }

    @GetMapping("/get-topic-posts")
    public ResponseEntity<PageResponse<PostResponse>> getTopicPosts(
            @RequestParam UUID topicId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
            ) throws TopicNotFoundException {
        return ResponseEntity.ok(postService.getTopicPosts(topicId, pageNo, pageSize));
    }

    //POST
    @PostMapping("/create-topic")
    public ResponseEntity<TopicResponse> createTopic(@RequestBody TopicRequest topicRequest, @RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(postService.createTopic(topicRequest, userId));
    }

    //PUT
    @PutMapping("/create-post")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, @RequestParam UUID topicId, @RequestParam UUID userId) throws UserNotFoundException, TopicNotFoundException {
        return ResponseEntity.ok(postService.createPost(postRequest, topicId, userId));
    }

    @PutMapping("/delete-post")
    public ResponseEntity<String> deletePost(@RequestParam UUID postId, @RequestParam UUID userId) throws UserNotFoundException, PostNotFoundException {
        return ResponseEntity.ok(postService.deletePost(postId, userId));
    }

    //DELETE
    @DeleteMapping("/delete-topic")
    public ResponseEntity<String> deleteTopic(@RequestParam UUID topicId, UUID userId) throws TopicNotFoundException {
        return ResponseEntity.ok(postService.deleteTopic(topicId, userId));
    }


}