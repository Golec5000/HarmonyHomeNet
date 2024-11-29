package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class VoteNotFoundException extends RuntimeException {
    public VoteNotFoundException(String message) {
        super(message);
    }
}
