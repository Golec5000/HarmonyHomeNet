package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Post;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Topic;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PostRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.TopicRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PostService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.TopicResponse;
import jakarta.transaction.Transactional;
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
    @Transactional
    public TopicResponse createTopic(TopicRequest topicRequest, UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Topic topic = Topic.builder()
                .title(topicRequest.getTitle())
                .user(user)
                .build();

        if (user.getTopics() == null) user.setTopics(new ArrayList<>());
        user.getTopics().add(topic);

        Topic saved = topicRepository.save(topic);

        return TopicResponse.builder()
                .id(saved.getUuidID())
                .title(saved.getTitle())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<TopicResponse> getUserTopics(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) throw new UserNotFoundException("User: " + userId + " not found");

        return topicRepository.findByUserUuidID(userId).stream()
                .map(
                        topic -> TopicResponse.builder()
                                .id(topic.getUuidID())
                                .title(topic.getTitle())
                                .createdAt(topic.getCreatedAt())
                                .build()
                )
                .toList();
    }

    @Override
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(
                        topic -> TopicResponse.builder()
                                .id(topic.getUuidID())
                                .title(topic.getTitle())
                                .createdAt(topic.getCreatedAt())
                                .userName(topic.getUser().getFirstName() + " " + topic.getUser().getLastName())
                                .build()
                )
                .toList();
    }

    @Override
    public String deleteTopic(UUID topicId) throws TopicNotFoundException {
        if (!topicRepository.existsByUuidID(topicId))
            throw new TopicNotFoundException("Topic: " + topicId + " not found");

        topicRepository.deleteById(topicId);
        return "Topic deleted successfully";
    }

    @Override
    @Transactional
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
        user.getPosts().add(post);

        if (topic.getPosts() == null) topic.setPosts(new ArrayList<>());
        topic.getPosts().add(post);

        Post saved = postRepository.save(post);

        return PostResponse.builder()
                .id(saved.getUuidID())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .userName(user.getFirstName() + " " + user.getLastName())
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
                                .userName(post.getUser().getFirstName() + " " + post.getUser().getLastName())
                                .build()
                )
                .toList();
    }

    @Override
    @Transactional
    public String deletePost(UUID postId, UUID userId) throws UserNotFoundException, PostNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post: " + postId + " not found"));

        if (!postRepository.canDeletePost(postId, userId, user.getRole().name()))
            throw new IllegalArgumentException("User: " + userId + " is not authorized to delete the post");

        user.getPosts().remove(post);
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
                                .userName(post.getUser().getFirstName() + " " + post.getUser().getLastName())
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
                                .userName(post.getUser().getFirstName() + " " + post.getUser().getLastName())
                                .build()
                )
                .toList();
    }
}
