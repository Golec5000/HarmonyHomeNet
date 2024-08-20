package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super("Post not found: " + message);
    }
}
