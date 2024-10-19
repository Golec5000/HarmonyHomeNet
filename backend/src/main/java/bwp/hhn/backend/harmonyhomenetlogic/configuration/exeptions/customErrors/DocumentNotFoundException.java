package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super("Document is not found" + message);
    }
}
