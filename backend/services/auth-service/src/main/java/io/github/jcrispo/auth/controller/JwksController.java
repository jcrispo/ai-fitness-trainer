package io.github.jcrispo.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import io.github.jcrispo.auth.config.JwksConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwksController {

    private final JWKSet jwkSet;
    public JwksController(JwksConfig cfg) { this.jwkSet = cfg.getJwkSet(); }

    @GetMapping("/jwks.json")
    public Map<String, Object> keys() {
        return jwkSet.toJSONObject();
    }
}
