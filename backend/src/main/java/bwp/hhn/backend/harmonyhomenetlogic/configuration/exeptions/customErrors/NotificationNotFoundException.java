package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message) {
        super("Notification not found: " + message);
    }
}
