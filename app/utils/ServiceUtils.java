package utils;

import actions.Attributes;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.typesafe.config.Config;
import exceptions.RequestException;
import models.User;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Http;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ServiceUtils {

//    @Inject
//    static
//    IMongoDB mongoDB;

//    @Inject
//    static
//    Config config;
    public static String getTokenFrom (Http.Request request) {
        Optional<String> optionalToken = request.getHeaders().get("token");
        return optionalToken.orElse(null);
    }

//    public static List<String> getRoles(User user) {
//        return user.getRoles()
//                .stream()
//                .map(x ->Role.getName())
//                .collect(Collectors.toList());
//    }
    public static User getUserFrom(Http.Request request) {
        return request.attrs().get(Attributes.USER_TYPED_KEY);
    }


    public static CompletableFuture<String> decodeToken(String token) {

        return CompletableFuture.supplyAsync(() -> {
                    byte[] decoded = Base64.getDecoder().decode(token.split("\\.")[1]);
                    String decodedString = new String(decoded);
                    JsonNode node = play.libs.Json.parse(decodedString);
                    String id = node.get("iss").asText();

                    return id;
                }
        );
    }

    public static CompletableFuture<User> getUserFromId(IMongoDB mongoDB, String id) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> collection = mongoDB
                    .getMongoDatabase()
                    .getCollection("user", User.class);
            User user = collection.find(Filters.eq("_id", new ObjectId(id))).first();

            if (user == null) {
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, Json.toJson("User not found!")));
            }
            return user;
        });
    }

    public static CompletableFuture<User> verify(Config config, User user, String token) {
        return CompletableFuture.supplyAsync(() -> {
            String secret = config.getString("play.http.secret.key");
            Algorithm algorithm = null;
            try {
                algorithm = Algorithm.HMAC256(secret);
            } catch (UnsupportedEncodingException e) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, e.getMessage()));
            }
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(user.getId().toString())
                    .build();
            verifier.verify(token);
            return user;
        });
    }

    public static String getTokenFromRequest(Http.Request request) {
        Optional<String> optionalToken = request.getHeaders().get("token");
        return optionalToken.orElse(null);
    }


}