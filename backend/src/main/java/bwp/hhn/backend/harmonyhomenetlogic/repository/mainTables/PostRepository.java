package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUserUuidID(UUID userId);

    boolean existsPostByUserUuidID(UUID userId);

    List<Post> findByTopicUuidID(UUID topicId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Post p WHERE p.uuidID = :postId AND (p.user.uuidID = :userId OR :role IN ('EMPLOYEE', 'ADMIN'))")
    boolean canDeletePost(@Param("postId") UUID postId, @Param("userId") UUID userId, @Param("role") String role);
}