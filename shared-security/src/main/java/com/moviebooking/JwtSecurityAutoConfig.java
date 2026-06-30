package com.moviebooking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@AutoConfiguration
public class JwtSecurityAutoConfig {

    @Bean
    @Primary
    public RSAPublicKey rsaPublicKey(@Value("${jwt.public-key}") Resource res) throws Exception {
        String pem = new String(res.getInputStream().readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        var keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pem));
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    @Bean
    @ConditionalOnMissingBean
    JWTService jwtService(RSAPublicKey rsaPublicKey){
        return new JWTService(rsaPublicKey);
    }

    @Bean
    @ConditionalOnMissingBean
    public JWTAuthProvider jwtAuthProvider(JWTService jwtService) {
        return new JWTAuthProvider(jwtService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManager(JWTAuthProvider provider) {
        return new ProviderManager(List.of(provider));
    }

    @Bean
    @ConditionalOnMissingBean
    public JWTFilter jwtFilter(AuthenticationManager authenticationManager) {
        return new JWTFilter(authenticationManager);
    }
}
