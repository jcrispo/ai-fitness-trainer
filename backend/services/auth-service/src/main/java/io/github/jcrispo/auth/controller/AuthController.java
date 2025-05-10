package io.github.jcrispo.auth.controller;

import com.nimbusds.jose.jwk.RSAKey;
import io.github.jcrispo.auth.config.JwksConfig;
import io.github.jcrispo.auth.dto.LoginRequest;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtEncoder encoder;
    private final JwksConfig jwksConfig;

    public AuthController(JwtEncoder encoder, JwksConfig cfg) {
        this.encoder = encoder;
        this.jwksConfig = cfg;
    }

    public Map<String, Object> login(@RequestBody LoginRequest req) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://auth-service:8081")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(req.username())
                .claim("roles", List.of("ROLE_USER"))
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwksConfig.getRsaKey().getKeyID())
                .build();

        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600
        );
    }
}
