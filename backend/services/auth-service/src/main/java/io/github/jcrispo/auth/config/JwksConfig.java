package io.github.jcrispo.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@Getter
public class JwksConfig {
    private final RSAKey rsaKey;
    private final JWKSet jwkSet;

   public JwksConfig() throws NoSuchAlgorithmException {

       KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA");
       kpGenerator.initialize(2048);
       KeyPair kp = kpGenerator.generateKeyPair();
       RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
       RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();

       this.rsaKey = new RSAKey.Builder(pub)
               .privateKey(priv)
               .keyID(UUID.randomUUID().toString())
               .build();
       this.jwkSet = new JWKSet(rsaKey);
   }

   @Bean
   public JWKSource<SecurityContext> jwkSource() {
       return (selector, context) -> selector.select(jwkSet);
   }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
