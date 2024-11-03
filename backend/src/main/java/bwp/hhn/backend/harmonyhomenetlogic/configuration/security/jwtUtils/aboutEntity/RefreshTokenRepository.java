package bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    @Query(value = "SELECT rt FROM RefreshToken rt " +
            "JOIN rt.user u " +
            "WHERE u.email = :userEmail AND rt.revoked = false")
    List<RefreshToken> findAllRefreshTokenByUserEmail(String userEmail);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true")
    void deleteAllRevokedTokens();
}