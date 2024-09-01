package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class BasementNotFoundException extends RuntimeException {
    public BasementNotFoundException(String message) {
        super("Basement not found: " + message);
    }
}
