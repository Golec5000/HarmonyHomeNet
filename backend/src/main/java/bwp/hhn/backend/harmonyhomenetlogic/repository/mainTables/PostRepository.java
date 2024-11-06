package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByUserUuidID(UUID userId, Pageable pageable);

    boolean existsPostByUserUuidID(UUID userId);

    Page<Post> findByTopicUuidID(UUID topicId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Post p WHERE p.uuidID = :postId AND (p.user.uuidID = :userId OR :role IN ('EMPLOYEE', 'ADMIN'))")
    boolean canDeletePost(@Param("postId") UUID postId, @Param("userId") UUID userId, @Param("role") String role);
}