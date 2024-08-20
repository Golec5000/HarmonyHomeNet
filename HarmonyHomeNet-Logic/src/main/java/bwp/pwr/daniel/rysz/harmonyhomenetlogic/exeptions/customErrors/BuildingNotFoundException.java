package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class BuildingNotFoundException extends RuntimeException {
    public BuildingNotFoundException(String message) {
        super("Building is not found: " + message);
    }
}
