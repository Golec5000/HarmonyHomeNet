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
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.TopicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public PageResponse<TopicResponse> getAllTopics(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Topic> topics = topicRepository.findAll(pageable);

        return getTopicResponsePageResponse(topics);
    }

    @Override
    public String deleteTopic(UUID topicId, UUID userId) throws TopicNotFoundException {
        if (!topicRepository.existsByUuidID(topicId))
            throw new TopicNotFoundException("Topic: " + topicId + " not found");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        //czyli jesli nie jest rolą pracowniczą to wyzaca błąd autoryzacji
        if(user.getRole().getLevel() < 2)
            throw new IllegalArgumentException("User: " + userId + " is not authorized to delete the topic");

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
    public PageResponse<PostResponse> getTopicPosts(UUID topicId, int pageNo, int pageSize) throws TopicNotFoundException {
        if (!topicRepository.existsById(topicId)) throw new TopicNotFoundException("Topic: " + topicId + " not found");

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> posts = postRepository.findByTopicUuidID(topicId, pageable);

        return getPostResponsePageResponse(posts);
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

    private PageResponse<TopicResponse> getTopicResponsePageResponse(Page<Topic> topics) {
        return new PageResponse<>(
                topics.getNumber(),
                topics.getSize(),
                topics.getTotalPages(),
                topics.getContent().stream()
                        .map(
                                topic -> TopicResponse.builder()
                                        .id(topic.getUuidID())
                                        .title(topic.getTitle())
                                        .createdAt(topic.getCreatedAt())
                                        .userName(topic.getUser() != null ? topic.getUser().getFirstName() + " " + topic.getUser().getLastName() : null)
                                        .build()
                        )
                        .toList(),
                topics.isLast(),
                topics.hasNext(),
                topics.hasPrevious()
        );
    }

    private PageResponse<PostResponse> getPostResponsePageResponse(Page<Post> posts) {
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalPages(),
                posts.getContent().stream()
                        .map(
                                post -> PostResponse.builder()
                                        .id(post.getUuidID())
                                        .content(post.getContent())
                                        .createdAt(post.getCreatedAt())
                                        .userName(post.getUser() != null ? post.getUser().getFirstName() + " " + post.getUser().getLastName() : null)
                                        .build()
                        )
                        .toList(),
                posts.isLast(),
                posts.hasNext(),
                posts.hasPrevious()
        );
    }
}
