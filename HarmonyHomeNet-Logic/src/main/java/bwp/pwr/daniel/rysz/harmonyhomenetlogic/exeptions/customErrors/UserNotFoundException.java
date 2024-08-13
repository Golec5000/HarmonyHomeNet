package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User is not found: " + message);
    }
}
