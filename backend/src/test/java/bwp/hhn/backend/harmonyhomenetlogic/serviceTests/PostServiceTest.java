package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PostServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.*;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.TopicResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

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
        // Given
        TopicRequest topicRequest = TopicRequest.builder()
                .title("New Topic")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TopicResponse response = postService.createTopic(topicRequest, userId);

        // Then
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
        // Given
        TopicRequest topicRequest = TopicRequest.builder()
                .title("New Topic")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> postService.createTopic(topicRequest, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void testGetAllTopics_Success() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Topic> topics = Collections.singletonList(topic);
        Page<Topic> topicPage = new PageImpl<>(topics, pageable, topics.size());

        when(topicRepository.findAll(pageable)).thenReturn(topicPage);

        // When
        PageResponse<TopicResponse> response = postService.getAllTopics(pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("Test Topic", response.content().get(0).title());

        verify(topicRepository, times(1)).findAll(pageable);
    }

    @Test
    void testDeleteTopic_Success() throws TopicNotFoundException, UserNotFoundException {
        // Given
        when(topicRepository.existsByUuidID(topicId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setRole(Role.ROLE_EMPLOYEE);

        // When
        String result = postService.deleteTopic(topicId, userId);

        // Then
        assertEquals("Topic deleted successfully", result);

        verify(topicRepository, times(1)).existsByUuidID(topicId);
        verify(topicRepository, times(1)).deleteById(topicId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteTopic_NotFound() {
        // Given
        when(topicRepository.existsByUuidID(topicId)).thenReturn(false);

        // When & Then
        assertThrows(TopicNotFoundException.class, () -> postService.deleteTopic(topicId, userId));

        verify(topicRepository, times(1)).existsByUuidID(topicId);
        verifyNoMoreInteractions(topicRepository);
    }

    @Test
    void testDeleteTopic_UserNotAuthorized() {
        // Given
        when(topicRepository.existsByUuidID(topicId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setRole(Role.ROLE_OWNER); // Not authorized

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> postService.deleteTopic(topicId, userId));

        verify(topicRepository, times(1)).existsByUuidID(topicId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(topicRepository);
    }

    @Test
    void testCreatePost_Success() throws UserNotFoundException, TopicNotFoundException {
        // Given
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostResponse response = postService.createPost(postRequest, topicId, userId);

        // Then
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
        // Given
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> postService.createPost(postRequest, topicId, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(topicRepository);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testCreatePost_TopicNotFound() {
        // Given
        PostRequest postRequest = PostRequest.builder()
                .content("New Post Content")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TopicNotFoundException.class, () -> postService.createPost(postRequest, topicId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(topicRepository, times(1)).findById(topicId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testGetTopicPosts_Success() throws TopicNotFoundException {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(topicRepository.existsById(topicId)).thenReturn(true);
        when(postRepository.findByTopicUuidID(topicId, pageable)).thenReturn(postPage);

        // When
        PageResponse<PostResponse> response = postService.getTopicPosts(topicId, pageNo, pageSize);

        // Then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("Test Content", response.content().get(0).content());

        verify(topicRepository, times(1)).existsById(topicId);
        verify(postRepository, times(1)).findByTopicUuidID(topicId, pageable);
    }

    @Test
    void testGetTopicPosts_TopicNotFound() {
        // Given
        int pageNo = 0;
        int pageSize = 10;

        when(topicRepository.existsById(topicId)).thenReturn(false);

        // When & Then
        assertThrows(TopicNotFoundException.class, () -> postService.getTopicPosts(topicId, pageNo, pageSize));

        verify(topicRepository, times(1)).existsById(topicId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testDeletePost_Success() throws UserNotFoundException, PostNotFoundException {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.canDeletePost(postId, userId, user.getRole().name())).thenReturn(true);

        // When
        String result = postService.deletePost(postId, userId);

        // Then
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
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testDeletePost_NotAuthorized() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.canDeletePost(postId, userId, user.getRole().name())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> postService.deletePost(postId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).canDeletePost(postId, userId, user.getRole().name());
    }
}
