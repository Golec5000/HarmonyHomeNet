package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class ParkingSpaceNotFoundException extends RuntimeException {
    public ParkingSpaceNotFoundException(String message) {
        super("Parking space not found: " + message);
    }
}
