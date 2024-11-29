package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors;

public class ProblemReportNotFoundException extends RuntimeException {
    public ProblemReportNotFoundException(String message) {
        super(message);
    }
}
