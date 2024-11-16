package bwp.hhn.backend.harmonyhomenetlogic.utils.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ROLE_OWNER(1),
    ROLE_EMPLOYEE(2),
    ROLE_ADMIN(3);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
