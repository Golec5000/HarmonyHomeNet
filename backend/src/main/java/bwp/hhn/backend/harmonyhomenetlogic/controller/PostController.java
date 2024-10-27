package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PostService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/forum")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // GET
    @GetMapping("/get-user-topics")
    public ResponseEntity<List<TopicResponse>> getUserTopics(@RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(postService.getUserTopics(userId));
    }

    @GetMapping("/get-all-topics")
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        return ResponseEntity.ok(postService.getAllTopics());
    }

    @GetMapping("/get-topic-posts")
    public ResponseEntity<List<PostResponse>> getTopicPosts(@RequestParam UUID topicId) throws TopicNotFoundException {
        return ResponseEntity.ok(postService.getTopicPosts(topicId));
    }

    @GetMapping("/get-user-posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }

    @GetMapping("/get-all-posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
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
    public ResponseEntity<String> deleteTopic(@RequestParam UUID topicId) throws TopicNotFoundException {
        return ResponseEntity.ok(postService.deleteTopic(topicId));
    }


}