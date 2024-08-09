package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors;

public class DocumentNotFoundException extends Exception {
    public DocumentNotFoundException(String message) {
        super("Document is not found" + message);
    }
}
