package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
