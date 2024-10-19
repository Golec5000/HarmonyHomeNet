package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User is not found: " + message);
    }
}
