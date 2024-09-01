package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException(String message) {
        super("Apartment is not found: " + message);
    }
}
