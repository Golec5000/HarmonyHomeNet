package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class PollNotFoundException extends RuntimeException {
    public PollNotFoundException(String message) {
        super("Poll is not found: " + message);
    }
}
