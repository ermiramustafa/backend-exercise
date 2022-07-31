package services;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import io.jsonwebtoken.*;
import models.User;
import play.libs.concurrent.HttpExecutionContext;

import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Singleton
public class AuthenticationService {
    @Inject
    HttpExecutionContext ec;

    @Inject
    Config config;

    public CompletableFuture<HashMap<String, String>> generateToken(User user) {
        return CompletableFuture.supplyAsync(() -> {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key"));
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            JwtBuilder builder = Jwts.builder()
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                    .claim("content", user.getUsername())
                    .signWith(signatureAlgorithm, signingKey);

            String jwt = builder.compact();
            HashMap<String, String> result = new HashMap<>();
            result.put("token", jwt);

            return result;
        }, ec.current());
    }

    public String parseToken(String jwt) {
        try{
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key")))
                    .parseClaimsJws(jwt)
                    .getBody().get("content", String.class);
        } catch (SignatureException | ExpiredJwtException ex) {
            throw ex;
        }

    }
}
