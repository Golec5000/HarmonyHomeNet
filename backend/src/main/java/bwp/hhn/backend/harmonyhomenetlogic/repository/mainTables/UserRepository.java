package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);

    boolean existsByUuidID (UUID uuidID);

    @Query("SELECT u FROM User u WHERE u.uuidID = :id AND (u.role = 'ROLE_ADMIN' OR u.role = 'ROLE_EMPLOYEE')")
    Optional<User> findByIdAndRole(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.uuidID = :id AND u.role = 'ROLE_OWNER'")
    Optional<User> findByIdAndRoleUser(@Param("id") UUID id);

    Optional<User> findByUuidIDOrEmail (UUID uuidID, String email);

    boolean existsByEmail(@NotEmpty @Email @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format") String email);

    Optional<User> findByResetToken(String resetToken);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.resetToken = null, u.resetTokenExpiry = null WHERE u.resetTokenExpiry <= :now")
    void deleteAllExpiredResetTokens(Instant now);
}