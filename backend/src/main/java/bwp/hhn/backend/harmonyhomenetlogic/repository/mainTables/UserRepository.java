package bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);

    boolean existsByUuidID (UUID uuidID);

    @Query("SELECT u FROM User u WHERE u.uuidID = :id AND (u.role = 'ADMIN' OR u.role = 'EMPLOYEE')")
    Optional<User> findByIdAndRole(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.uuidID = :id AND u.role = 'OWNER'")
    Optional<User> findByIdAndRoleUser(@Param("id") UUID id);

    Optional<User> findByUuidIDOrEmail (UUID uuidID, String email);

}