package bwp.hhn.backend.harmonyhomenetlogic.configuration.security;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshToken;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.security.jwtUtils.aboutEntity.RefreshTokenRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!authHeader.startsWith(TokenType.Bearer.name())) {
            return;
        }

        final String refreshToken = authHeader.substring(7);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if (storedRefreshToken != null) {
            String userEmail = storedRefreshToken.getUser().getEmail();
            List<RefreshToken> userTokens = refreshTokenRepository.findAllRefreshTokenByUserEmail(userEmail);

            userTokens.forEach(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }
}
