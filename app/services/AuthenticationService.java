package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.typesafe.config.Config;
import exceptions.RequestException;
import models.AuthenticationModel;
import models.User;
import mongo.IMongoDB;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import javax.inject.Singleton;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Singleton
public class AuthenticationService {
    @Inject
    HttpExecutionContext ec;

    @Inject
    Config config;

    @Inject
    IMongoDB mongoDB;

    @Inject UserService userService;

//    public CompletableFuture<HashMap<String, String>> generateToken(User user) {
//        return CompletableFuture.supplyAsync(() -> {
//            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key"));
//            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
//
//            JwtBuilder builder = Jwts.builder()
//                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
//                    .claim("content", user.getUsername())
//                    .signWith(signatureAlgorithm, signingKey);
//
//            String jwt = builder.compact();
//            HashMap<String, String> result = new HashMap<>();
//            result.put("token", jwt);
//
//            return result;
//        }, ec.current());
//    }

//    public String parseToken(String jwt) {
//        try{
//            return Jwts.parser()
//                    .setSigningKey(DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key")))
//                    .parseClaimsJws(jwt)
//                    .getBody().get("content", String.class);
//        } catch (SignatureException | ExpiredJwtException ex) {
//            throw ex;
//        }
//
//    }
    public CompletableFuture<String> authenticate(AuthenticationModel login) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                MongoCollection<User> collection = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class);
                String secret = config.getString("play.http.secret.key");
                Algorithm algorithm = Algorithm.HMAC256(secret);

                if(Strings.isNullOrEmpty(login.getUsername()) || Strings.isNullOrEmpty(login.getPassword())) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "Empty fields");
                }

                User u1 = collection.find(Filters.and(
                        Filters.eq("username", login.getUsername()),
                        Filters.eq("password", login.getPassword())
                )).first();

//                if(Hash.checkPassword(login.getPassword(), u1.getPassword())) {
//                    throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, Json.toJson("Bad Credentials!")));
//                }

                return JWT.create()
                        .withIssuer(u1.getId().toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                        .sign(algorithm);

            }catch (Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "Could not fetch data!"));
            }
        }, ec.current());
    }

}




















