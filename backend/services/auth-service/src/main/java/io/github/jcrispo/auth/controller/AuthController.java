package io.github.jcrispo.auth.controller;

import io.github.jcrispo.auth.config.JwksConfig;
import io.github.jcrispo.auth.dto.LoginRequest;
import io.github.jcrispo.auth.dto.RegisterRequest;
import io.github.jcrispo.auth.model.User;
import io.github.jcrispo.auth.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder encoder;
    private final JwksConfig jwksConfig;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder encoder, JwksConfig cfg) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encoder = encoder;
        this.jwksConfig = cfg;
    }

    @PostMapping("/login")
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

    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
        }

        String hashed = passwordEncoder.encode(req.password());
        User user = User.builder()
                .username(req.username())
                .passwordHash(hashed)
                .roles("ROLES_USER") //TODO: move roles to a separate table
                .build();

        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }
}
