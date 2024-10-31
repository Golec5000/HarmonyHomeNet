package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
