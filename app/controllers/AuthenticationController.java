package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.User;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class AuthenticationController extends Controller {
    /*public Result authenticate(Http.Request request) {
        JsonNode node = request.body().asJson();
        User user = Json.fromJson(node, User.class);

        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        String jwtToken = Jwts.builder()
                .claim("name", "Ermira Mustafa")
                .claim("email", "ermiramustafa@gmail.com")
                .setSubject("ermira")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(51, ChronoUnit.MINUTES)))
                .compact();

        return ok(views.html.index.render());
        //return ok(Json.toJson(jwtToken));
       // return jwtToken;
    }

    /*public Result authenticate(Http.Request request) {
        JsonNode node = request.body().asJson();
        User user = Json.fromJson(node, User.class);
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ok(views.html.index.render());
    }*/

    public Result authenticate(Http.Request request) {
        JsonNode node =request.body().asJson();
        try {
            User user = new User("ermiramustafa", "password");
            user.setId(new ObjectId());
            String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withClaim("id", String.valueOf(user.getId()))
                    .sign(algorithm);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ok(views.html.index.render());
    }

}
