package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.TopicResponse;

import java.util.List;
import java.util.UUID;

public interface PostService {

    TopicResponse createTopic(TopicRequest topicRequest, UUID userId) throws UserNotFoundException;

    List<TopicResponse> getUserTopics(UUID userId) throws UserNotFoundException;

    List<TopicResponse> getAllTopics();

    String deleteTopic(UUID topicId) throws TopicNotFoundException;

    PostResponse createPost(PostRequest postRequest, UUID topicId, UUID userId) throws UserNotFoundException, TopicNotFoundException;

    List<PostResponse> getTopicPosts(UUID topicId) throws TopicNotFoundException;

    String deletePost(UUID postId, UUID userId) throws UserNotFoundException, PostNotFoundException;

    List<PostResponse> getUserPosts(UUID userId) throws UserNotFoundException;

    List<PostResponse> getAllPosts();

}
