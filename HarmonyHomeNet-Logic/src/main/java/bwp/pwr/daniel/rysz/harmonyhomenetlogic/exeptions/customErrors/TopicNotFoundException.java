package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class TopicNotFoundException extends Exception {
    public TopicNotFoundException(String message) {
        super("Topic not found: " +message);
    }
}
