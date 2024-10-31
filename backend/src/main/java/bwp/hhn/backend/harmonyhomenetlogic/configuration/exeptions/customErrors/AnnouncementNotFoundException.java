package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class AnnouncementNotFoundException extends RuntimeException {
    public AnnouncementNotFoundException(String message) {
        super(message);
    }
}
