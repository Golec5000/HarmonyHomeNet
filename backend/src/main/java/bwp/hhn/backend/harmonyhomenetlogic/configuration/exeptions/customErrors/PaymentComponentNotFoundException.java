package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class PaymentComponentNotFoundException extends RuntimeException {
    public PaymentComponentNotFoundException(String message) {
        super(message);
    }
}
