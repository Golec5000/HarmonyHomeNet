package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Service
public class BankingServiceImp implements BankingService {

    private static final Logger LOGGER = Logger.getLogger(BankingServiceImp.class.getName());

    private final String account = "12345123451234512345698723";

    @Override
    public void pay(BigDecimal amount) {
        LOGGER.info("Paying " + amount + " to " + account);
    }
}
