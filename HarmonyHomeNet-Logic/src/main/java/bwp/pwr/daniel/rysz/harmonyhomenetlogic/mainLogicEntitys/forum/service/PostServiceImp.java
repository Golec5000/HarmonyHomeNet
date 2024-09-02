package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.PostNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Topic;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.repository.PostRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service.UserService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.PostResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private TopicService topicService;
    private UserService userService;


    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public PostResponse save(@NonNull Post post) {
        postRepository.save(post);
        return mapPostToPostResponse(post);
    }

    @Override
    public void deleteById(UUID id) throws PostNotFoundException {
        if (postRepository.existsById(id)) postRepository.deleteById(id);
        else throw new PostNotFoundException("wrong post id");
    }

    @Override
    public Post findById(UUID id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("wrong post id"));
    }

    @Override
    public List<Post> findPostByUserLogin(@NonNull String login) throws PostNotFoundException {
        return postRepository.findByUserLogin(login);
    }

    @Override
    public PostResponse mapPostToPostResponse(@NonNull Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .postAuthor(post.getUser().getLogin())
                .postContent(post.getPostContent())
                .build();
    }

    @Override
    public List<PostResponse> mapPostListToPostResponseList(@NonNull List<Post> posts) {
        return posts.stream()
                .map(this::mapPostToPostResponse)
                .toList();
    }

    @Override
    public PostResponse addPostToTopic(UUID topicId, String userLogin, @NonNull Post post) throws UserNotFoundException, PostNotFoundException {
        Topic topic = topicService.findById(topicId);
        User user = userService.findByLogin(userLogin);

        if (topic.getPosts() == null) topic.setPosts(new ArrayList<>());
        if (user.getPosts() == null) user.setPosts(new ArrayList<>());

        topic.getPosts().add(post);
        user.getPosts().add(post);

        postRepository.save(post);
        topicService.save(topic);
        userService.save(user);

        return mapPostToPostResponse(post);

    }


    @Override
    public void deletePostFromTopic(UUID topicId, UUID postId) throws PostNotFoundException {
        Topic topic = topicService.findById(topicId);
        Post post = findById(postId);

        User user = post.getUser();

        user.getPosts().remove(post);
        topic.getPosts().remove(post);

        deleteById(postId);
        topicService.save(topic);
        userService.save(user);
    }
}
