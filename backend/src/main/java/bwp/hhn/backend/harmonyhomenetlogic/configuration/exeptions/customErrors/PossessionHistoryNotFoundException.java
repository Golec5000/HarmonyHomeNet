package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class PossessionHistoryNotFoundException extends RuntimeException {
    public PossessionHistoryNotFoundException(String message) {
        super("Possession History not found: " + message);
    }
}
