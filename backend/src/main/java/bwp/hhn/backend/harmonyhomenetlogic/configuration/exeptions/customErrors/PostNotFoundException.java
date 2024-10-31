package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
