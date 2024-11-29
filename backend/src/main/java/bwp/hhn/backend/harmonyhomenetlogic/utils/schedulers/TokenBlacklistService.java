package bwp.hhn.backend.harmonyhomenetlogic.utils.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = new HashSet<>();
    private final JwtDecoder jwtDecoder;

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    @Scheduled(cron = "0 */15 * * * *") // Every hour
    @Async
    public void clearExpiredTokens() {
        blacklistedTokens.removeIf(this::isTokenExpired);
    }

    private boolean isTokenExpired(String token) {
        return Date.from(
                Objects.requireNonNull(
                        jwtDecoder.decode(token).getExpiresAt()
                )
        ).before(new Date());
    }
}