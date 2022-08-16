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
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import utils.Hash;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Results.notFound;

public class AuthenticationService {
    @Inject
    HttpExecutionContext ec;

    @Inject
    Config config;

    @Inject
    IMongoDB mongoDB;

    /**
     * Creates a token that contains user informaton
     *
     * @param login
     * @return token
     */
    public CompletableFuture<String> authenticate(AuthenticationModel login) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("ketuuu");
                MongoCollection<User> collection = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class);
                String secret = config.getString("play.http.secret.key");
                Algorithm algorithm = Algorithm.HMAC256(secret);
                System.out.println("ketuuu22");
                if (Strings.isNullOrEmpty(login.getUsername()) || Strings.isNullOrEmpty(login.getPassword())) {
                    throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "Empty fields"));
                }
                System.out.println("ketuuu3");
                User u1 = collection.find(
                        Filters.eq("username", login.getUsername())
                ).first();
                System.out.println("ketuuu4");

                if (u1 == null) {
                    throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "This user doesn't exist!"));
//                    return CompletableFuture.completedFuture(notFound("This user doesn't exist!"));
                }
                System.out.println("ketu 55");

                if (!Hash.checkPassword(login.getPassword(), u1.getPassword())) {
                    throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, Json.toJson("Bad Credentials!")));
                }

                System.out.println("id e userit" + u1);

                return JWT.create()
                        .withIssuer(u1.getId().toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                        .sign(algorithm);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "Could not fetch data-test!"));
            }
        }, ec.current());
    }

    /*
    public CompletableFuture<String> getToken(Http.RequestHeader request) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                String token = request.getHeaders().get("token").get();
                return pToken(token);
            } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
                throw new CompletionException(new RequestException(FORBIDDEN, ex.getMessage()));
            }
        }, ec.current());
    }

    public User userFromToken(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token.split("\\.")[1]);
        String decodedToken = new String(decodedBytes);
        String id = Json.parse(decodedToken).get("iss").asText();

        MongoCollection<User> collection = mongoDB.getMongoDatabase()
                .getCollection("users", User.class);

        User user = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return user;
    }*/

}




















