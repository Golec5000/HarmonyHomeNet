package bwp.hhn.backend.harmonyhomenetlogic.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AccessLevel {
    ACCESS_DENIED(0),  // 000 - brak uprawnień
    READ(1),           // 001 - odczyt
    WRITE(2),          // 010 - zapis
    DELETE(4);         // 100 - usunięcie

    private final int level;


    public static boolean hasPermission(int permissionMask, AccessLevel requiredLevel) {
        return (permissionMask & requiredLevel.level) == requiredLevel.level;
    }

    public static int addPermission(int permissionMask, AccessLevel requiredLevel) {
        return permissionMask | requiredLevel.level;
    }

    public static int removePermission(int permissionMask, AccessLevel requiredLevel) {
        return permissionMask & ~requiredLevel.level;
    }

}

