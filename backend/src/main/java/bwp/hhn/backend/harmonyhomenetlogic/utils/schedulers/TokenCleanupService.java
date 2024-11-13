package bwp.hhn.backend.harmonyhomenetlogic.utils.schedulers;

import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Async
    public void cleanUpExpiredResetTokens() {
        userRepository.deleteAllExpiredResetTokens(Instant.now());
    }

}
