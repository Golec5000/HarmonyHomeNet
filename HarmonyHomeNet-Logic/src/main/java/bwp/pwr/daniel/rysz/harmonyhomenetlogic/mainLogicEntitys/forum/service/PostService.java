package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.PostNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Post;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.forumStaff.PostResponse;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    List<Post> findAll();

    PostResponse save(@NonNull Post post);

    void deleteById(UUID id) throws PostNotFoundException;

    Post findById(UUID id) throws PostNotFoundException;

    List<Post> findPostByUserLogin(@NonNull String login) throws PostNotFoundException;

    PostResponse mapPostToPostResponse(@NonNull Post post);

    List<PostResponse> mapPostListToPostResponseList(@NonNull List<Post> posts);

    PostResponse addPostToTopic(UUID topicId, String userLogin, @NonNull Post post) throws UserNotFoundException, PostNotFoundException;

    void deletePostFromTopic(UUID topicId, UUID postId) throws PostNotFoundException;
}
