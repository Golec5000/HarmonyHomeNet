package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Post;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Topic;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PostRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.TopicRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements PostService {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    @Override
    public TopicResponse createTopic(TopicRequest topicRequest, UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Topic topic = Topic.builder()
                .title(topicRequest.getTitle())
                .user(user)
                .build();

        if (user.getTopics() == null) user.setTopics(new ArrayList<>());
        else user.getTopics().add(topic);
        topicRepository.save(topic);

        return TopicResponse.builder()
                .id(topic.getUuidID())
                .title(topic.getTitle())
                .createdAt(topic.getCreatedAt())
                .build();
    }

    @Override
    public List<TopicResponse> getUserTopics(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) throw new UserNotFoundException("User: " + userId + " not found");

        return topicRepository.findByUserUuidID(userId).stream()
                .map(topic -> TopicResponse.builder()
                        .id(topic.getUuidID())
                        .title(topic.getTitle())
                        .createdAt(topic.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(topic -> TopicResponse.builder()
                        .id(topic.getUuidID())
                        .title(topic.getTitle())
                        .createdAt(topic.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public String deleteTopic(UUID topicId) throws TopicNotFoundException {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Topic: " + topicId + " not found"));

        topicRepository.delete(topic);
        return "Topic deleted successfully";
    }

    @Override
    public PostResponse createPost(PostRequest postRequest, UUID topicId, UUID userId) throws UserNotFoundException, TopicNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Topic: " + topicId + " not found"));

        Post post = Post.builder()
                .content(postRequest.getContent())
                .user(user)
                .topic(topic)
                .build();

        if (user.getPosts() == null) user.setPosts(new ArrayList<>());
        else user.getPosts().add(post);

        if (topic.getPosts() == null) topic.setPosts(new ArrayList<>());
        else topic.getPosts().add(post);

        userRepository.save(user);
        postRepository.save(post);
        topicRepository.save(topic);

        return PostResponse.builder()
                .id(post.getUuidID())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public List<PostResponse> getTopicPosts(UUID topicId) throws TopicNotFoundException {
        if (!topicRepository.existsById(topicId)) throw new TopicNotFoundException("Topic: " + topicId + " not found");

        return postRepository.findByTopicUuidID(topicId).stream()
                .map(
                        post -> PostResponse.builder()
                                .id(post.getUuidID())
                                .content(post.getContent())
                                .createdAt(post.getCreatedAt())
                                .build()
                )
                .toList();
    }

    @Override
    public String deletePost(UUID postId, UUID userId) throws UserNotFoundException, PostNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post: " + postId + " not found"));

        if (!postRepository.existsPostByUserUuidID(userId))
            throw new UserNotFoundException("User: " + userId + " is not the owner of the post");

        user.getPosts().removeIf(p -> p.getUuidID().equals(postId));
        userRepository.save(user);
        postRepository.delete(post);

        return "Post deleted successfully";
    }

    @Override
    public List<PostResponse> getUserPosts(UUID userId) throws UserNotFoundException {

        if (!userRepository.existsById(userId)) throw new UserNotFoundException("User: " + userId + " not found");

        return postRepository.findByUserUuidID(userId).stream()
                .map(
                        post -> PostResponse.builder()
                                .id(post.getUuidID())
                                .content(post.getContent())
                                .createdAt(post.getCreatedAt())
                                .build()
                )
                .toList();
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(
                        post -> PostResponse.builder()
                                .id(post.getUuidID())
                                .content(post.getContent())
                                .createdAt(post.getCreatedAt())
                                .build()
                )
                .toList();
    }
}
