package bwp.hhn.backend.harmonyhomenetlogic.utils.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_OWNER,
    ROLE_ADMIN,
    ROLE_EMPLOYEE;

    @Override
    public String getAuthority() {
        return name();
    }
}
