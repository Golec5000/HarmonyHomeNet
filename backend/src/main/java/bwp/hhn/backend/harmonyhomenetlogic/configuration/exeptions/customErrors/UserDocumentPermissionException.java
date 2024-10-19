package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class UserDocumentPermissionException extends RuntimeException {
    public UserDocumentPermissionException(String message) {
        super("UserDocumentPermission not found: " + message);
    }
}
