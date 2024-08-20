package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class ForumNotFoundException extends RuntimeException {
    public ForumNotFoundException(String message) {
        super("Forum not found: " + message);
    }
}
