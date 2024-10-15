package bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables;

import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.UserDocumentPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocumentPermissionRepository extends JpaRepository<UserDocumentPermission, String> {
}