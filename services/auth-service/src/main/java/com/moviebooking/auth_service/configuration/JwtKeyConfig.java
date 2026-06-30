package com.moviebooking.auth_service.configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Bean
    @Primary
    public RSAPrivateKey rsaPrivateKey(@Value("${jwt.private-key}") Resource res) throws Exception {
        String pem = new String(res.getInputStream().readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        var keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem));
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

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
}