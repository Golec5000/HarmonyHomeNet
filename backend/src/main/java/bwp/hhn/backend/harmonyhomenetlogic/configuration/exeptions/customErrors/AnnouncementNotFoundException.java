package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class AnnouncementNotFoundException extends RuntimeException {
    public AnnouncementNotFoundException(String message) {
        super("Announcement not found: " + message);
    }
}
