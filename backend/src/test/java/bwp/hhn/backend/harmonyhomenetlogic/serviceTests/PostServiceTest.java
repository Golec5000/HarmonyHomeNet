package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Post;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Topic;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PostRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.TopicRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.PostServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.TopicResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImp postServiceImp;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createTopicTest() throws UserNotFoundException {
        // Przygotowanie danych
        UUID userId = UUID.randomUUID();
        TopicRequest topicRequest = new TopicRequest();
        topicRequest.setTitle("Testowy Temat");

        User user = User.builder()
                .uuidID(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Wykonanie metody
        TopicResponse topicResponse = postServiceImp.createTopic(topicRequest, userId);

        // Weryfikacja
        assertNotNull(topicResponse);
        assertEquals("Testowy Temat", topicResponse.title());
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    public void createTopic_UserNotFound() {
        UUID userId = UUID.randomUUID();
        TopicRequest topicRequest = new TopicRequest();
        topicRequest.setTitle("Testowy Temat");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.createTopic(topicRequest, userId);
        });
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    public void getUserTopicsTest() throws UserNotFoundException {
        UUID userId = UUID.randomUUID();
        Topic topic1 = Topic.builder()
                .uuidID(UUID.randomUUID())
                .title("Temat 1")
                .build();
        Topic topic2 = Topic.builder()
                .uuidID(UUID.randomUUID())
                .title("Temat 2")
                .build();

        List<Topic> topics = Arrays.asList(topic1, topic2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(topicRepository.findByUserUuidID(userId)).thenReturn(topics);

        List<TopicResponse> topicResponses = postServiceImp.getUserTopics(userId);

        assertNotNull(topicResponses);
        assertEquals(2, topicResponses.size());
        verify(topicRepository, times(1)).findByUserUuidID(userId);
    }

    @Test
    public void getUserTopics_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.getUserTopics(userId);
        });
        verify(topicRepository, never()).findByUserUuidID(userId);
    }

    @Test
    public void getAllTopicsTest() {
        Topic topic1 = Topic.builder()
                .uuidID(UUID.randomUUID())
                .title("Temat 1")
                .build();
        Topic topic2 = Topic.builder()
                .uuidID(UUID.randomUUID())
                .title("Temat 2")
                .build();

        List<Topic> topics = Arrays.asList(topic1, topic2);

        when(topicRepository.findAll()).thenReturn(topics);

        List<TopicResponse> topicResponses = postServiceImp.getAllTopics();

        assertNotNull(topicResponses);
        assertEquals(2, topicResponses.size());
        verify(topicRepository, times(1)).findAll();
    }

    @Test
    public void deleteTopicTest() throws TopicNotFoundException {
        UUID topicId = UUID.randomUUID();

        Topic topic = Topic.builder()
                .uuidID(topicId)
                .build();

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        String result = postServiceImp.deleteTopic(topicId);

        assertEquals("Topic deleted successfully", result);
        verify(topicRepository, times(1)).delete(topic);
    }

    @Test
    public void deleteTopic_TopicNotFound() {
        UUID topicId = UUID.randomUUID();

        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> {
            postServiceImp.deleteTopic(topicId);
        });
        verify(topicRepository, never()).delete(any(Topic.class));
    }

    @Test
    public void createPostTest() throws UserNotFoundException, TopicNotFoundException {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        PostRequest postRequest = new PostRequest();
        postRequest.setContent("Testowa Treść");

        User user = User.builder()
                .uuidID(userId)
                .posts(new ArrayList<>())
                .build();

        Topic topic = Topic.builder()
                .uuidID(topicId)
                .posts(new ArrayList<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        PostResponse postResponse = postServiceImp.createPost(postRequest, topicId, userId);

        assertNotNull(postResponse);
        assertEquals("Testowa Treść", postResponse.content());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(userRepository, times(1)).save(user);
        verify(topicRepository, times(1)).save(topic);
    }

    @Test
    public void createPost_UserNotFound() throws TopicNotFoundException {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        PostRequest postRequest = new PostRequest();
        postRequest.setContent("Testowa Treść");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(new Topic()));

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.createPost(postRequest, topicId, userId);
        });
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    public void createPost_TopicNotFound() throws UserNotFoundException {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        PostRequest postRequest = new PostRequest();
        postRequest.setContent("Testowa Treść");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> {
            postServiceImp.createPost(postRequest, topicId, userId);
        });
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    public void getTopicPostsTest() throws TopicNotFoundException {
        UUID topicId = UUID.randomUUID();

        Post post1 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 1")
                .build();

        Post post2 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 2")
                .build();

        List<Post> posts = Arrays.asList(post1, post2);

        when(topicRepository.existsById(topicId)).thenReturn(true);
        when(postRepository.findByTopicUuidID(topicId)).thenReturn(posts);

        List<PostResponse> postResponses = postServiceImp.getTopicPosts(topicId);

        assertNotNull(postResponses);
        assertEquals(2, postResponses.size());
        verify(postRepository, times(1)).findByTopicUuidID(topicId);
    }

    @Test
    public void getTopicPosts_TopicNotFound() {
        UUID topicId = UUID.randomUUID();

        when(topicRepository.existsById(topicId)).thenReturn(false);

        assertThrows(TopicNotFoundException.class, () -> {
            postServiceImp.getTopicPosts(topicId);
        });
        verify(postRepository, never()).findByTopicUuidID(topicId);
    }

    @Test
    public void deletePostTest() throws UserNotFoundException, PostNotFoundException {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .uuidID(userId)
                .posts(new ArrayList<>())
                .build();

        Post post = Post.builder()
                .uuidID(postId)
                .user(user)
                .build();

        user.getPosts().add(post);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.existsPostByUserUuidID(userId)).thenReturn(true);

        String result = postServiceImp.deletePost(postId, userId);

        assertEquals("Post deleted successfully", result);
        assertFalse(user.getPosts().contains(post));
        verify(userRepository, times(1)).save(user);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void deletePost_PostNotFound() throws UserNotFoundException {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .uuidID(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> {
            postServiceImp.deletePost(postId, userId);
        });
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    public void deletePost_UserNotFound() throws PostNotFoundException {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.deletePost(postId, userId);
        });
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    public void deletePost_UserNotOwner() throws UserNotFoundException, PostNotFoundException {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .uuidID(userId)
                .build();

        Post post = Post.builder()
                .uuidID(postId)
                .user(User.builder().uuidID(UUID.randomUUID()).build()) // Inny użytkownik
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.existsPostByUserUuidID(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.deletePost(postId, userId);
        });
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    public void getUserPostsTest() throws UserNotFoundException {
        UUID userId = UUID.randomUUID();

        Post post1 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 1")
                .build();

        Post post2 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 2")
                .build();

        List<Post> posts = Arrays.asList(post1, post2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(postRepository.findByUserUuidID(userId)).thenReturn(posts);

        List<PostResponse> postResponses = postServiceImp.getUserPosts(userId);

        assertNotNull(postResponses);
        assertEquals(2, postResponses.size());
        verify(postRepository, times(1)).findByUserUuidID(userId);
    }

    @Test
    public void getUserPosts_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            postServiceImp.getUserPosts(userId);
        });
        verify(postRepository, never()).findByUserUuidID(userId);
    }

    @Test
    public void getAllPostsTest() {
        Post post1 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 1")
                .build();

        Post post2 = Post.builder()
                .uuidID(UUID.randomUUID())
                .content("Post 2")
                .build();

        List<Post> posts = Arrays.asList(post1, post2);

        when(postRepository.findAll()).thenReturn(posts);

        List<PostResponse> postResponses = postServiceImp.getAllPosts();

        assertNotNull(postResponses);
        assertEquals(2, postResponses.size());
        verify(postRepository, times(1)).findAll();
    }
}
