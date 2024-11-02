package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PostServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.*;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImp postService;

    private User user;
    private Topic topic;
    private Post post;
    private UUID userId;
    private UUID topicId;
    private UUID postId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        topicId = UUID.randomUUID();
        postId = UUID.randomUUID();

        user = User.builder()
                .uuidID(userId)
                .firstName("testuser")
                .lastName("User")
                .email("testuser@example.com")
                .posts(new ArrayList<>())
                .topics(new ArrayList<>())
                .role(Role.ROLE_OWNER)
                .build();

        topic = Topic.builder()
                .uuidID(topicId)
                .title("Test Topic")
                .user(user)
                .posts(new ArrayList<>())
                .build();

        post = Post.builder()
                .uuidID(postId)
                .content("Test Content")
                .user(user)
                .topic(topic)
                .build();
    }

    @Test
    void testCreateTopic_Success() throws UserNotFoundException {
        TopicRequest topicRequest = TopicRequest.builder()
                .title("New Topic")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TopicResponse response = postService.createTopic(topicRequest, userId);

        assertNotNull(response);
        assertEquals("New Topic", response.title());

        // Verify that the topic was added to the user's topics list
        assertThat(user.getTopics()).hasSize(1);
        assertThat(user.getTopics().get(0).getTitle()).isEqualTo("New Topic");

        verify(userRepository, times(1)).findById(userId);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void testCreateTopic_UserNotFound() {
        TopicRequest topicRequest = TopicRequest.builder()
                .title("New Topic")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.createTopic(topicRequest, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void testGetUserTopics_Success() throws UserNotFoundException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(topicRepository.findByUserUuidID(userId)).thenReturn(Collections.singletonList(topic));

        List<TopicResponse> topics = postService.getUserTopics(userId);

        assertNotNull(topics);
        assertEquals(1, topics.size());
        assertEquals("Test Topic", topics.get(0).title());

        verify(userRepository, times(1)).existsById(userId);
        verify(topicRepository, times(1)).findByUserUuidID(userId);
    }

    @Test
    void testGetUserTopics_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> postService.getUserTopics(userId));

        verify(userRepository, times(1)).existsById(userId);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void testGetAllTopics() {
        when(topicRepository.findAll()).thenReturn(Collections.singletonList(topic));

        List<TopicResponse> topics = postService.getAllTopics();

        assertNotNull(topics);
        assertEquals(1, topics.size());
        assertEquals("Test Topic", topics.get(0).title());

        verify(topicRepository, times(1)).findAll();
    }

    @Test
    void testDeleteTopic_Success() throws TopicNotFoundException {
        when(topicRepository.existsByUuidID(topicId)).thenReturn(true);
        doNothing().when(topicRepository).deleteById(topicId);

        String result = postService.deleteTopic(topicId);

        assertEquals("Topic deleted successfully", result);

        verify(topicRepository, times(1)).existsByUuidID(topicId);
        verify(topicRepository, times(1)).deleteById(topicId);
    }

    @Test
    void testDeleteTopic_NotFound() {
        when(topicRepository.existsByUuidID(topicId)).thenReturn(false);

        assertThrows(TopicNotFoundException.class, () -> postService.deleteTopic(topicId));

        verify(topicRepository, times(1)).existsByUuidID(topicId);
        verify(topicRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void testCreatePost_Success() throws UserNotFoundException, TopicNotFoundException {
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostResponse response = postService.createPost(postRequest, topicId, userId);

        assertNotNull(response);
        assertEquals("New Post Content", response.content());

        // Verify that the post was added to the user's and topic's posts list
        assertThat(user.getPosts()).hasSize(1);
        assertThat(topic.getPosts()).hasSize(1);
        assertThat(user.getPosts().get(0).getContent()).isEqualTo("New Post Content");
        assertThat(topic.getPosts().get(0).getContent()).isEqualTo("New Post Content");

        verify(userRepository, times(1)).findById(userId);
        verify(topicRepository, times(1)).findById(topicId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.createPost(postRequest, topicId, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(topicRepository);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testCreatePost_TopicNotFound() {
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> postService.createPost(postRequest, topicId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(topicRepository, times(1)).findById(topicId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testGetTopicPosts_Success() throws TopicNotFoundException {
        when(topicRepository.existsById(topicId)).thenReturn(true);
        when(postRepository.findByTopicUuidID(topicId)).thenReturn(Collections.singletonList(post));

        List<PostResponse> posts = postService.getTopicPosts(topicId);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Test Content", posts.get(0).content());

        verify(topicRepository, times(1)).existsById(topicId);
        verify(postRepository, times(1)).findByTopicUuidID(topicId);
    }

    @Test
    void testGetTopicPosts_TopicNotFound() {
        when(topicRepository.existsById(topicId)).thenReturn(false);

        assertThrows(TopicNotFoundException.class, () -> postService.getTopicPosts(topicId));

        verify(topicRepository, times(1)).existsById(topicId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testDeletePost_Success() throws UserNotFoundException, PostNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.canDeletePost(postId, userId, user.getRole().name())).thenReturn(true);

        String result = postService.deletePost(postId, userId);

        assertEquals("Post deleted successfully", result);

        // Verify that the post was removed from the user's posts list
        assertThat(user.getPosts()).doesNotContain(post);

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).canDeletePost(postId, userId, user.getRole().name());
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void testDeletePost_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testDeletePost_NotAuthorized() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.canDeletePost(postId, userId, user.getRole().name())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).canDeletePost(postId, userId, user.getRole().name());
    }

    @Test
    void testGetUserPosts_Success() throws UserNotFoundException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(postRepository.findByUserUuidID(userId)).thenReturn(Collections.singletonList(post));

        List<PostResponse> posts = postService.getUserPosts(userId);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Test Content", posts.get(0).content());

        verify(userRepository, times(1)).existsById(userId);
        verify(postRepository, times(1)).findByUserUuidID(userId);
    }

    @Test
    void testGetUserPosts_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> postService.getUserPosts(userId));

        verify(userRepository, times(1)).existsById(userId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testGetAllPosts() {
        when(postRepository.findAll()).thenReturn(Collections.singletonList(post));

        List<PostResponse> posts = postService.getAllPosts();

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Test Content", posts.get(0).content());

        verify(postRepository, times(1)).findAll();
    }
}
