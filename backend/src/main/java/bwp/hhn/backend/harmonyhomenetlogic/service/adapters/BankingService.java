package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import java.math.BigDecimal;

public interface BankingService {
    void pay(BigDecimal amount);
}
