package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PostNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.TopicNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PostRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.TopicRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PostResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.TopicResponse;

import java.util.UUID;

public interface PostService {

    TopicResponse createTopic(TopicRequest topicRequest, UUID userId) throws UserNotFoundException;

    PageResponse<TopicResponse> getUserTopics(UUID userId, int pageNo, int pageSize) throws UserNotFoundException;

    PageResponse<TopicResponse> getAllTopics(int pageNo, int pageSize);

    String deleteTopic(UUID topicId) throws TopicNotFoundException;

    PostResponse createPost(PostRequest postRequest, UUID topicId, UUID userId) throws UserNotFoundException, TopicNotFoundException;

    PageResponse<PostResponse> getTopicPosts(UUID topicId, int pageNo, int pageSize) throws TopicNotFoundException;

    String deletePost(UUID postId, UUID userId) throws UserNotFoundException, PostNotFoundException;

    PageResponse<PostResponse> getUserPosts(UUID userId, int pageNo, int pageSize) throws UserNotFoundException;

    PageResponse<PostResponse> getAllPosts(int pageNo, int pageSize);

}
