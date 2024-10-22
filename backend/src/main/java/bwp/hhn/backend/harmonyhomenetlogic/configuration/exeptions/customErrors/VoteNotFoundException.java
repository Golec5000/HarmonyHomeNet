package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class VoteNotFoundException extends RuntimeException {
    public VoteNotFoundException(String message) {
        super("Vote is not found" + message);
    }
}
