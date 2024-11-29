package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Service
public class BankingServiceImp implements BankingService {

    private static final Logger LOGGER = Logger.getLogger(BankingServiceImp.class.getName());

    @Override
    public void pay(BigDecimal amount, String account) {
        LOGGER.info("Paying " + amount + " to " + account);
    }
}
